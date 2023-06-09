package com.example.shop.receive;

import com.alibaba.fastjson.JSON;
import com.example.shop.contants.QueueConstants;
import com.example.shop.entity.AliSms;
import com.example.shop.utils.SmsUtils;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author: William
 * @date: 2023-06-06 10:46
 **/
@Configuration
@RequiredArgsConstructor
public class SmsReceiveListener {

    private final SmsUtils smsUtils;

    @RabbitListener(queues = {QueueConstants.PHONE_SMS_QUEUE})
    public void receivePhoneSmsQueueMessage(Message message, Channel channel) {
        //接收消息，发送短信操作
        String json = new String(message.getBody());

        //转换成实体类对象
        AliSms aliSms = JSON.parseObject(json, AliSms.class);

        //发送短信
        try {
            boolean flag = true;
            //boolean flag = smsUtils.sendSms(
            //        aliSms.getPhonenum(),
            //        aliSms.getCode(),
            //        aliSms.getUserId()
            //);

            if (flag)
                //消费消息
                    channel.basicAck(
                            message.getMessageProperties().getDeliveryTag(),
                            false
                    );
        /*} catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
