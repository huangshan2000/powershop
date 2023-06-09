package com.example.shop.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: William
 * @date: 2023-06-07 00:07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "wx.msg")
public class WxProperties {
    private String appId;
    private String appSecret;
    private String templateId;
    private String templateContent;
    private String getTokenUrl;
    private String getTokenUrlType;
    private String sendMsgUrl;
    private String sendMsgUrlType;
}
