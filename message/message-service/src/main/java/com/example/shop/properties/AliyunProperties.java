package com.example.shop.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: William
 * @date: 2023-06-05 23:03
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperties {
    private String signName;
    private String templateCode;
    private String templateContent;
    private String accessKeyId;
    private String accessKeySecret;
    private String regionId;
    private String endPoint;
}
