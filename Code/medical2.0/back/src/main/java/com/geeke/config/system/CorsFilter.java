package com.geeke.config.system;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component  // 跨域filter处理
public class CorsFilter implements Filter {

    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CorsFilter.class);

    /**
     * 允许的跨域来源，多个用逗号分隔。默认允许本地开发。
     * 生产环境应通过环境变量 CORS_ALLOWED_ORIGINS 配置具体域名。
     */
    @Value("${cors.allowed-origins:http://localhost:7020,http://127.0.0.1:7020}")
    private String allowedOrigins;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");

        // 允许的来源列表
        List<String> allowedOriginList = Arrays.asList(allowedOrigins.split(","));

        // 如果配置为 * 则允许所有来源（不推荐与 Allow-Credentials 一起使用）
        if ("*".equals(allowedOrigins.trim())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
        } else if (origin != null && allowedOriginList.contains(origin.trim())) {
            // 反射请求的 Origin，支持带凭证的跨域请求
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Token, Authorization");

        // 预检请求直接返回
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
    public void init(FilterConfig filterConfig) {}
    public void destroy() {}
}