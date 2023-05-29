package com.example.shop.config;

import cn.hutool.core.io.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.nio.charset.Charset;

/**
 * @author: William
 * @date: 2023-05-26 17:00
 **/

@Configuration
//开启资源服务器：请求必须携带token
@EnableResourceServer
//开启方法级别的权限控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

        //公钥读取到内存中
        String publicKey = FileUtil.readString("rsa/sz2212.txt", Charset.defaultCharset());

        //根据公钥来进行token令牌的验证配置
        jwtAccessTokenConverter.setVerifierKey(publicKey);

        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(
                jwtAccessTokenConverter()
        );
    }

    /**
     * 使用公钥来进行token的验证
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(
                tokenStore()
        );
    }

    /**
     *  放行的资源：
     *      允许跨域访问
     *      放行的资源：actuator(健康检查)、druid(德鲁伊数据库监控)、swagger...
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().disable();
        http.authorizeRequests()
                .antMatchers("/v2/api-docs",  // swagger  druid ...
                        "/v3/api-docs",
                        "/swagger-resources/configuration/ui",  //用来获取支持的动作
                        "/swagger-resources",                   //用来获取api-docs的URI
                        "/swagger-resources/configuration/security",//安全选项
                        "/webjars/**",
                        "/swagger-ui/**",
                        "/druid/**",
                        "/actuator/**")
                .permitAll()
                .anyRequest()
                .authenticated();

    }
}
