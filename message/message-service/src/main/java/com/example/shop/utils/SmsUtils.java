package com.example.shop.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.example.shop.domain.SmsLog;
import com.example.shop.mapper.SmsLogMapper;
import com.example.shop.properties.AliyunProperties;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: William
 * @date: 2023-06-05 23:01
 **/
@Configuration
@RequiredArgsConstructor
public class SmsUtils {
    private final AliyunProperties aliyunProperties;

    private final SmsLogMapper smsLogMapper;

    public boolean sendSms(String phoneNum,String code,String userId) throws ExecutionException, InterruptedException {

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(aliyunProperties.getAccessKeyId())
                .accessKeySecret(aliyunProperties.getAccessKeySecret())
                //.securityToken("<your-token>") // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region(aliyunProperties.getRegionId()) // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(aliyunProperties.getEndPoint())
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .templateCode(aliyunProperties.getTemplateCode())
                .signName(aliyunProperties.getSignName())
                .phoneNumbers(phoneNum)
                .templateParam("{\"code\":"+code+"}")
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        // Synchronously get the return value of the API request
        SendSmsResponse resp = response.get();
        String respCode = resp.getBody().getCode();
        System.out.println(new Gson().toJson(resp));

        //记录日志信息
        //当有费用产生时，哪怕再小的金额我们也要有迹可循
        if(StringUtils.equals(respCode,"OK")){
            //短信发送成功，记录日志信息
            smsLogMapper.insert(
                    SmsLog.builder()
                            .userId(userId)
                            .userPhone(phoneNum)
                            .content(
                                    String.format(aliyunProperties.getTemplateContent(),code)
                            )
                            .mobileCode(code)
                            .type(2)
                            .recDate(new Date())
                            .responseCode(respCode)
                            .status(respCode.equals("OK")?1:0)
                            .build()
            );
        }

        // Finally, close the client
        client.close();

        return respCode.equals("OK") ? true : false;
    }
}
