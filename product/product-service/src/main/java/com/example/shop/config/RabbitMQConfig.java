package com.example.shop.config;

import com.example.shop.contants.QueueConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @author: William
 * @date: 2023-06-02 00:36
 **/
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue esChangeQueue() {
        return new Queue(QueueConstants.ES_CHANGE_QUEUE);
    }
}
