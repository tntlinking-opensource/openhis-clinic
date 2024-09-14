package com.geeke.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
    private String cacheType;
    private String host;
    private String port;
    private String password;
    private int database;
    private int timeout;
}
