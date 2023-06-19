package com.example.shop.contants;

public interface QueueConstants {

    /**
     * es的快速导入的监听队列
     */
    String ES_CHANGE_QUEUE = "es.change.queue";

    /**
     * 发送短信的监听队列
     */
    String PHONE_SMS_QUEUE = "phone.sms.queue";

    /**
     * 发送微信公众号消息的监听队列
     */
    String WX_MSG_QUEUE = "wx.msg.queue";

    /**
     * 延迟队列
     */
    String ORDER_MS_QUEUE = "order.ms.queue";

    /**
     * 死信队列
     */
    String ORDER_DEAD_QUEUE = "order.dead.queue";

    String ORDER_DEAD_EX = "order.dead.ex";

    String ORDER_DEAD_KEY = "order.dead.key";

}
