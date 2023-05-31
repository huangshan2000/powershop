package com.example.shop.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: William
 * @date: 2023-05-31 00:50
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties("minio")
public class MinIOProperties {
    private String endpoint;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}
