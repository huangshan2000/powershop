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

}
