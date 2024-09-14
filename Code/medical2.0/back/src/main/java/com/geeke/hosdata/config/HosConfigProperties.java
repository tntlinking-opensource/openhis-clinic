package com.geeke.hosdata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.hosconfig")
public class HosConfigProperties {
    private String hosDataServerUrl;
    private String hosTokenUrl;
    private String AppId;
    private String OrganizeId;

}
