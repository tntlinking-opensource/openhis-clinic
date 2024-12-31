package com.geeke;

import com.bstek.ureport.console.UReportServlet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import javax.servlet.Servlet;

/**
 * @author: lys
 * @description: SpringBoot启动类
 * @date: 2017/10/24 11:55
 */
@SpringBootApplication
@ImportResource("classpath:ureport-console-context.xml")
@MapperScan("com.geeke.**.dao")
public class MyApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MyApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
    @Bean
    public ServletRegistrationBean<Servlet> buildUReportServlet() {
        return new ServletRegistrationBean<>(new UReportServlet(), "/ureport/*");
    }
}
