package com.example.shop.config;

import com.example.shop.contants.AuthServerConstants;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * @author: William
 * @date: 2023-05-25 15:24
 **/

/**
 * 授权服务器
 *      配置oauth2第三方账号信息(授权方式：密码授权、客户端授权)
 *      token生成方式：jet+Rsa密匙对
 */
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //密码加密
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    //认证资源管理对象
    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
        //后台管理系统的配置第三方信息，密码授权
                .withClient("web")
                .secret(passwordEncoder.encode("web-secret"))
                .authorizedGrantTypes("password")
                .accessTokenValiditySeconds(21600)
                .scopes("all")
                .redirectUris("https://www.baidu.com")
                .and()
                //前台商城的配置第三方信息，客户端授权
                .withClient("power")
                .secret("power-secret")
                .authorizedGrantTypes("client_credentials")
                .accessTokenValiditySeconds(Integer.MAX_VALUE)
                .scopes("all")
                .redirectUris("https://www.baidu.com")
                ;
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        //创建jwt令牌生成器对象
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

        //设置非对称加密的生成令牌,将私钥读取到内存中,获取密匙工厂对象
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource(AuthServerConstants.PRIVATE_KEY_PATH), AuthServerConstants.PASSWORD.toCharArray());

        //获取密匙对对象
        KeyPair keyPair = factory.getKeyPair(AuthServerConstants.PASSWORD);

        //通过私钥生成jwt令牌
        jwtAccessTokenConverter.setKeyPair(keyPair);

        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter())
                ;
    }
}
