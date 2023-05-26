package com.example.shop.filters;

import com.example.shop.constant.GatewayConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: William
 * @date: 2023-05-25 09:19
 **/

/**
 * 网关过滤器
 * 根据请求路径区分是登录操作还是非登录操作
 * 如果是登录操作
 * 放行，在路由转发中，将请求转发到auth-server微服务，登录成功后返回token信息
 * 将token存入到redis中
 * 如果是非登录操作
 * 获取请求头信息，Authorization
 * 如果包含，放行，将校验操作，交给微服务进行token的合法性校验
 * 如果不包含，返回没有权限的错误信息
 */

/**
 * 将部分常用的字符串数据存入到新建的接口GatewayConstants中，便于开发。
 *      1. 配置过滤器CustomFilter类，实现GlobalFilter, Ordered接口
 *      2. 获取请求路径，.getRequest().getURI().getPath()
 *      3. 判断请求路径是否为空，并根据请求路径区分是登录操作还是非登录操作
 *      4. 为登录操作时，
 *          4.1 放行，在路由转发中，将请求转发到auth-server微服务，
 *          4.2 登录成功后返回token信息
 *      5. 为非登录操作时，获取请求头信息，Authorization，
 *          5.1 当请求头信息Authorization不为空时，传递了请求的令牌信息，
 *          5.2 校验令牌[token]格式
 *      6. 放行，交给微服务进行token的合法性校验
 *      7. 设置返回的json数据，状态码为401[HttpStatus.UNAUTHORIZED]
 *      8. 创建map集合，封装返回的错误信息
 *          8.1 以键值对的形式返回code和msg信息
 *          8.2 设置map集合的字节数组
 *          8.3 用户未授权，没有权限
 *              8.3.1 将错误提示信息的map集合写回到浏览器
 *              8.3.2 创建一个对象，进行写回操作Mono.just
 */
@Configuration
public class CustomFilter implements GlobalFilter, Ordered {
    /**
     * 拦截器的业务逻辑
     * 实现简单的权限校验操作
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求路径
        ServerHttpRequest request = exchange.getRequest();
        String path = exchange.getRequest().getURI().getPath();

        if (StringUtils.isNotBlank(path)) {
            //登录操作
            if (GatewayConstants.WHITE_LIST.contains(path))
                //放行
                return chain.filter(exchange);

            //非登录操作 bearer token 或 Bearer token
            String authorization = request.getHeaders().getFirst(GatewayConstants.AUTHORIZATION_PREFIX);
            if (StringUtils.isNotBlank(authorization)) {
                //传递了请求的令牌信息
                //校验令牌格式
                if (StringUtils.containsAny(authorization, GatewayConstants.BEARER_PREFIX, GatewayConstants.BEARER_LOWER_CASE_PREFIX))
                    //放行，交给微服务进行token的合法性校验
                    return chain.filter(exchange);
            }
        }

        ServerHttpResponse response = exchange.getResponse();

        //设置返回的json数据格式
        //UNAUTHORIZED(401, "Unauthorized"),
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(GatewayConstants.CONTENT_TYPE_PREFIX, GatewayConstants.APPLICATION_JSON_UTF8);


        //返回的错误信息
        Map<String, Object> errorResult = new HashMap<>();

        errorResult.put("code", 20401);
        errorResult.put("msg", "用户未授权，请重新登录");

        try {
            //设置map集合的字节数组
            //JSON.toJSONBytes(errorResult, SerializerFeature.WriteMapNullValue);
            byte[] bytes = new ObjectMapper().writeValueAsBytes(errorResult);
            //用户未授权，没有权限
            return response
                    //将错误提示信息的map集合写回到浏览器
                    .writeWith(
                            //创建一个对象，进行写回操作
                            Mono.just(
                                    response.bufferFactory().wrap(
                                            bytes
                                    )
                            )
                    );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
