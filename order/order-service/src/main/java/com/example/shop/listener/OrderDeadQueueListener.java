package com.example.shop.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shop.contants.QueueConstants;
import com.example.shop.domain.Order;
import com.example.shop.domain.ProdEsCount;
import com.example.shop.feign.ProductServiceFeign;
import com.example.shop.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: William
 * @date: 2023-06-19 20:49
 **/
@Configuration
@RequiredArgsConstructor
public class OrderDeadQueueListener {

    private final ProductServiceFeign productServiceFeign;

    private final RabbitTemplate rabbitTemplate;

    private final OrderService orderService;

    @RabbitListener(queues = {QueueConstants.ORDER_DEAD_QUEUE})
    public void receiveMsg4OrderDeadQueue(Message message, Channel channel) {

        //消息补回之前，如果用户已付款，那么不能够补回数据了
        Map<String, List<ProdEsCount>> dataMap = JSON.parseObject(new String(message.getBody()), Map.class);

        String orderNumber = "";

        //查询订单信息
        Order order = orderService.getOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNumber, orderNumber)
        );

        if (ObjectUtils.isNotEmpty(order))
            if (order.getIsPayed()>0)
                return;
        List<ProdEsCount> countList = null;

        for (Map.Entry<String, List<ProdEsCount>> entry : dataMap.entrySet()) {
            orderNumber = entry.getKey();
            countList = entry.getValue();
        }

        //补回Mysql库存
        //补回操作将count*-1，将负数的库存改为正数的库存
        countList.forEach(
                count -> count.setCount(
                        count.getCount() * -1
                )
        );

        //发送远程调用，扣减es库存信息
        productServiceFeign.deductMySqlStock(countList);

        //补回Es库存
        //发送消息到消息队列中(快速导入)
        rabbitTemplate.convertAndSend(
                QueueConstants.ES_CHANGE_QUEUE,
                JSON.toJSONString(countList)
        );

        //消费消息
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
