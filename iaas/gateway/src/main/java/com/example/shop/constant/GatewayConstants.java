package com.example.shop.constant;

import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;

public interface GatewayConstants {

    /**
     * 白名单集合
     */
    List<String> WHITE_LIST = Arrays.asList(
            //登录的请求路径
            "/oauth/token"
    );

    /**
     * 请求头
     */
    String AUTHORIZATION_PREFIX = "Authorization";
    String CONTENT_TYPE_PREFIX = "Content-Type";

    /**
     * 请求头Authorization的值的前缀
     *      Bearer token 或 bearer token
     */
    String BEARER_PREFIX = "Bearer ";
    String BEARER_LOWER_CASE_PREFIX = "bearer ";
    String APPLICATION_JSON_UTF8 = "application/json; charset=UTF-8";

    /**
     * jwtToken令牌前缀
     */
    String ACCESS_TOKEN_PREFIX = "access_token";
    String EXPIRES_IN_PREFIX = "expires_in";


    String JWT_TOKEN_PREFIX = "jwt:token:";
    String JWT_TOKEN_SUFFIX = "";
}
