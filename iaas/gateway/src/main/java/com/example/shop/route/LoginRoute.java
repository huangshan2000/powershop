package com.example.shop.route;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shop.constants.GatewayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/*
    登录的路由操作
        如果是登录的请求，那么拦截器会放行，请求就会到达我们当前的路由配置类中
            让登录的请求，转发到auth-server微服务中
            在响应回来的时候，将token存入到redis中
 */
@Configuration
public class LoginRoute {

    //缓存数据序列化缓存
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                //路由集合配置
                .routes()
                //登录的路由配置
                .route(
                        //路由的id
                        "toAuthServer",
                        //断言规则
                        predicate ->
                                predicate
                                        //拦截路径的规则
                                        .path(GatewayConstants.WHITE_LIST.get(0))
                                        //过滤器的规则
                                        .filters(
                                                //过滤器配置信息
                                                filter ->
                                                        filter
                                                                //修改响应体数据，拦截将响应体的数据存入到Redis中，再返回到浏览器中
                                                                .modifyResponseBody(
                                                                        //输入和输出的字节码类型
                                                                        String.class, String.class,
                                                                        //exchange对象，可以获取request或response对象
                                                                        //responseBody响应体数据 {access_token:xxx,expires_in:xxx,token_type:xxx,scope:xxx}
                                                                        (exchange, responseBody) -> {
                                                                            //将响应体的json数据进行解析，判断是否登录成功，如果登录成功，则返回的是token令牌
                                                                            JSONObject jsonObject = JSON.parseObject(responseBody);

                                                                            //判断是否包含返回的令牌数据
                                                                            if (jsonObject.containsKey(GatewayConstants.ACCESS_TOKEN_PREFIX) && jsonObject.containsKey(GatewayConstants.EXPIRES_IN_PREFIX)) {

                                                                                //获取token令牌和过期时间
                                                                                String token = jsonObject.getString(GatewayConstants.ACCESS_TOKEN_PREFIX);

                                                                                long expiresIn = jsonObject.getLongValue(GatewayConstants.EXPIRES_IN_PREFIX);

                                                                                //登录成功，将令牌和过期时间存入到redis中
                                                                                //redis在存储时，key不能超过1024字节，value不能超过512M
                                                                                redisTemplate.opsForValue().set(
                                                                                        GatewayConstants.JWT_TOKEN_PREFIX + token,
                                                                                        GatewayConstants.JWT_TOKEN_SUFFIX,
                                                                                        expiresIn,
                                                                                        TimeUnit.SECONDS
                                                                                );

                                                                            }
                                                                            //将响应体的数据，正常返回
                                                                            return Mono.just(responseBody);
                                                                        }
                                                                )
                                        )
                                        //将请求负载均衡转发到指定的微服务中
                                        .uri("lb://auth-server")
                )
                .build();
    }

}
