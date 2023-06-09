package com.example.shop.receive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shop.contants.QueueConstants;
import com.example.shop.entity.WxMsg;
import com.example.shop.properties.WxProperties;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


/**
 * @author: William
 * @date: 2023-06-09 11:55
 **/
@Configuration
@RequiredArgsConstructor
public class WxTemplateMsgReceiveListener {

    private final RestTemplate restTemplate;

    private final StringRedisTemplate redisTemplate;

    private final WxProperties wxProperties;

    @RabbitListener(queues = {QueueConstants.WX_MSG_QUEUE})
    public void receiveWxMsgListener(Message message, Channel channel) {

        //获取微信的token
        String accessToken = redisTemplate.opsForValue().get("wx:access:token");

        if (StringUtils.isBlank(accessToken))
            return;

        //接收传递过来的json数据
        String json = new String(message.getBody());

        //转换成实体类
        WxMsg wxMsg = JSON.parseObject(json, WxMsg.class);

        //凭借url地址信息
        String url = String.format(
                wxProperties.getSendMsgUrl(), accessToken
        );

        //发送消息
        String result = restTemplate.postForObject(
                //请求地址
                url,
                //请求体传递的参数信息，必须是实体类对象，因为RestTemplate会将该参数转换为json
                wxMsg,
                //返回值结果集
                String.class
        );

        JSONObject jsonObject = JSON.parseObject(result);

        if (jsonObject.containsKey("errorcode")) {
            int errcode = jsonObject.getIntValue("errcode");

            if (errcode == 0)
            //发送微信公众号消息成功，消费这条消息
            {
                try {
                    channel.basicAck(
                            message.getMessageProperties().getDeliveryTag(),
                            false
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
