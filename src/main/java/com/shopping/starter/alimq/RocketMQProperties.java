package com.shopping.starter.alimq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Properties;

/**
 * @desc 配置Bean
 */

@ConfigurationProperties(prefix = "aliyun.mq")
@Data
public class RocketMQProperties {

    private String onsAddr;

    private String topic;

    private String accessKey;

    private String secretKey;

    private Properties producer;

    private Properties consumer;

    private String tagSuffix;
}
