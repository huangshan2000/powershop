package com.example.shop.config;

import com.example.shop.contants.QueueConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: William
 * @date: 2023-06-18 17:18
 **/
@Configuration
public class RabbitMQConfig {

    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue orderMsQueue() {
        Map<String, Object> arguments = new HashMap<>();
        //设置死信队列相关信息
        arguments.put("x-message-ttl", 30000);
        arguments.put("x-dead-letter-exchange", QueueConstants.ORDER_DEAD_EX);
        arguments.put("x-dead-letter-routing-key", QueueConstants.ORDER_DEAD_KEY);

        Queue queue = new Queue(QueueConstants.ORDER_MS_QUEUE, true, false,false, arguments);
        return queue;
    }

    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue orderDeadQueue() {
        return new Queue(QueueConstants.ORDER_DEAD_QUEUE);
    }

    /**
     * 死信交换机
     * @return
     */
    public DirectExchange orderDeadEx() {
        return new DirectExchange(QueueConstants.ORDER_DEAD_EX);
    }

    @Bean
    public Binding orderDeadBindging() {
        return BindingBuilder.bind(orderDeadQueue()).to(orderDeadEx()).with(QueueConstants.ORDER_DEAD_KEY);
    }
}
