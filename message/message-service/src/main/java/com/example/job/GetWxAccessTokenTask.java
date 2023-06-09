package com.example.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shop.properties.WxProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author: William
 * @date: 2023-06-07 00:10
 **/
@Configuration
@RequiredArgsConstructor
public class GetWxAccessTokenTask {

    private final RestTemplate restTemplate;

    private final WxProperties wxProperties;

    private final RedisTemplate redisTemplate;

    @PostConstruct
    public void initAccessToken() {
        getWxAccessToken();
    }

    @Scheduled(initialDelay = 7000 * 1000, fixedDelay = 7000 * 1000)
    public void getWxAccessToken() {
        //获取微信公众号的全局token
        String url = String.format(
                wxProperties.getGetTokenUrl(),
                wxProperties.getAppId(),
                wxProperties.getAppSecret()
        );

        //发送请求，获取token
        String json = restTemplate.getForObject(url, String.class);

        System.out.println("json = " + json);

        JSONObject jsonObject = JSON.parseObject(json);

        if (jsonObject.containsKey("access-token")) {
            //获取成功
            String accessToken = jsonObject.getString("access_token");
            Long expiresIn = jsonObject.getLong("expires_in");

            //存入redis中
            redisTemplate.opsForValue().set("wx:access:token",accessToken,expiresIn, TimeUnit.SECONDS);
        }


    }
}
