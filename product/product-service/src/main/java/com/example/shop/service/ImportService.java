package com.example.shop.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

public interface ImportService {


    /**
     * 全量导入
     */
    void importAll();


    /**
     * 增量导入
     */
    void updateImport();


    /**
     * 快速导入
     */
    void quickImport(Message message, Channel channel);


}
