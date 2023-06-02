package com.example.shop.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: William
 * @date: 2023-06-02 15:26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties("wx.login")
public class WxProperties {
    private String appId;
    private String appSecret;
    private String loginUrl;
    private String loginUrlType;
}
