package com.example.shop.config;

import com.example.shop.contants.CommonContants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: William
 * @date: 2023-05-26 17:49
 **/

/**
 * Feign的拦截器，远程调用时向下传递令牌
 *      令牌传递的情况：
 *          1. 浏览器(token) -> 微服务A -> 微服务B -> 微服务C
 *          2. 手机端(token) -> 微服务A -> 微服务B -> 微服务C
 *              需要将当前的请求中的令牌获取到并向下传递，保证资源服务器对令牌的校验及合法性
 *          3. Queue -> 微服务D -> ...
 *          4. 回调方法(比如支付宝支付回调) -> 微服务E -> ...
 *              当前没有令牌被传递过来，需要生成一个客户端授权的令牌，我们在消息队列中接收到信息，远程调用微服务时，可以使用该令牌保证程序的运行
 *              公共的令牌，没有任何的权限信息，如何处理远程调用后的权限问题呢？
 *                  这个监听消息队列、回调方法，只会在商城的前台进行处理(手机端)，也可以不用校验，但是如果有人冒用了手机，也可以针对某个特定的请求进行判断处理
 */
@Configuration
public class RequestConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取请求对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (ObjectUtils.isNotEmpty(requestAttributes)) {
            //如果请求对象不为空，证明是浏览器或手机端发送的请求(通过正常的登录流程获取令牌)
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            if (ObjectUtils.isNotEmpty(request)) {
                //获取令牌
                String authorization = request.getHeader(CommonContants.AUTHORIZATION);
                //当前令牌不为空
                if (StringUtils.isNotBlank(authorization)) {
                    //将请求头向下传递
                    requestTemplate.header(CommonContants.AUTHORIZATION, authorization);


                    return;
                }
            }
            //请求头异常
            throw new RuntimeException(HttpStatus.UNAUTHORIZED.getReasonPhrase());

        }



        //如果请求对象为空，证明没有请求的出发，可能是通过监听消息队列发送的远程调用或者通过回调方法发送的远程调用(没有登录的入口，所以我们需要生成一个全局的令牌)
        //传递令牌信息
        requestTemplate.header(CommonContants.AUTHORIZATION, CommonContants.BEARER_TOKEN);

    }
}
