package com.example.shop.config;

import com.example.shop.contants.QueueConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author: William
 * @date: 2023-06-05 17:58
 **/
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue phoneSmsQueue() {
        return new Queue(QueueConstants.PHONE_SMS_QUEUE);
    }

    @Bean
    public Queue wxMsgQueue() {
        return new Queue(QueueConstants.WX_MSG_QUEUE);
    }
}
