package com.example.shop.controller;

import com.alibaba.fastjson.JSON;
import com.example.shop.base.BaseMember;
import com.example.shop.contants.QueueConstants;
import com.example.shop.domain.User;
import com.example.shop.entity.AliSms;
import com.example.shop.entity.R;
import com.example.shop.entity.WxMsg;
import com.example.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: William
 * @date: 2023-06-05 15:16
 **/
@RestController
@RequestMapping("/p/sms")
@RequiredArgsConstructor
public class MessageController extends BaseMember {

    private final StringRedisTemplate redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final UserService userService;

    @PostMapping("/send")
    public R<String> sendSmsMessage(@RequestBody AliSms aliSms) {

        //生成6位数的短信验证码
        String code = String.valueOf(RandomUtils.nextInt(100000, 999999));

        System.out.println("code = " + code);

        //reids中存入短信验证码
        //【xxx】您的验证码为xxxxxx，午五分钟有效...
        redisTemplate.opsForValue().set("bind:" + aliSms.getPhonenum(), code, 5, TimeUnit.MINUTES);

        //发送消息到消息队列中
        rabbitTemplate.convertAndSend(
                QueueConstants.PHONE_SMS_QUEUE,
                JSON.toJSONString(
                        aliSms.setCode(code)
                                .setUserId(
                                        getWxUserId()
                                )
                )
        );
        return ok("短信消息发送成功");
    }

    @PostMapping("/savePhone")
    public R<String> savePhone(@RequestBody AliSms aliSms, HttpServletRequest request) {

        //校验验证码的合法性
        String codeInRedis = redisTemplate.opsForValue().get("bind:" + aliSms.getPhonenum());

        if (!StringUtils.equals(codeInRedis, aliSms.getCode()))
            throw new RuntimeException("验证码不匹配");

        //根据userId更新用户手机号码
        User user = userService.getById(getWxUserId());

        //设置需要更新的数据
        boolean flag = userService.updateById(
                user.setUserMobile(aliSms.getPhonenum())
                        .setModifyTime(new Date())
                        .setUserLasttime(new Date())
                        .setUserLastip(request.getRemoteAddr())
        );

        return ok(
                flag ? "手机号码更新成功" : "手机号码更新失败"
        );
    }

    @GetMapping("/sendWx")
    public R<String> sendWx() {

        //根据userId查询微信模板公众号的openId
        User user = userService.getById(getWxUserId());

        //封装WxMsg
        WxMsg wxMsg = WxMsg.builder()
                .touser(user.getWxTemplateOpenId())
                .template_id("S9uJK2WI4aglegHMQP3jby5DeCouNslB_drZ0QSKaS0")
                .topcolor("#FF0000")
                .url("http://www.baidu.com")
                .build();
        //封装data，也就是微信公众号的具体显示的内容
        wxMsg.appenData("time","#173177", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        wxMsg.appenData("goods","#173177","海绵宝宝");
        wxMsg.appenData("price", "#173177", "100,000,000.00");
        wxMsg.appenData("money", "#173177", "10000,000,000.00");

        //发送到消息队列中
        rabbitTemplate.convertAndSend(
                QueueConstants.WX_MSG_QUEUE,
                JSON.toJSONString(wxMsg)
        );

        return ok(
                "微信公众号消息发送成功"
        );
    }
}
