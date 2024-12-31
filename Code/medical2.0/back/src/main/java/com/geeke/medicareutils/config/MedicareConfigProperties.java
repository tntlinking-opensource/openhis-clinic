package com.geeke.medicareutils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 医保接口配置参数
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.medicare")
public class MedicareConfigProperties {
    //是否开启医保接口
    private String check;
    //省份
    private String province;
    //医保接口地址
    private String url;

    //是否演示环境
    private String isDemo;
}
