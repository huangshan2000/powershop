package com.example.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author: William
 * @date: 2023-05-25 15:25
 **/

/**
 * 认证授权服务器
 */
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //允许跨域访问，默认是关闭的
        //跨域访问：当协议、IP、域名、端口，不一致时，就是跨域访问
        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().disable();

        //放行的资源配置，健康检查资源
        http.authorizeRequests()
                .antMatchers("/actuator/**")
                //允许访问不拦截
                .permitAll()
                //其他的请求
                .anyRequest()
                //必须登录才可以访问
                .authenticated();
    }
}
