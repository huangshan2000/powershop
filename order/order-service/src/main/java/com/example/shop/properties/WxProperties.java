package com.example.shop.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
