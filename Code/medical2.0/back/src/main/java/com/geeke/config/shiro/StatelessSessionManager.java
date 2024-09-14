package com.geeke.config.shiro;

/**
 * @Author lys
 * @Description redis服务配置
 * @Date 15:18 2019/12/20
 **/

import com.geeke.utils.SessionUtils;
import io.jsonwebtoken.Claims;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.geeke.utils.JwtUtils;
import com.geeke.utils.StringUtils;
import org.springframework.util.ObjectUtils;


public class StatelessSessionManager extends DefaultWebSessionManager {
    private final static Logger logger = LoggerFactory.getLogger(StatelessSessionManager.class);

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 这个是客户端请求给服务端带的header
     */
    private final static String HEADER_TOKEN_NAME = "X-Token";

    @Override
    protected Serializable getSessionId(ServletRequest servletRequest, ServletResponse servletResponse) {
        // 从请求头中获取token
        HttpServletRequest httpRequest = WebUtils.toHttp(servletRequest);
        String token = httpRequest.getHeader(HEADER_TOKEN_NAME);
        if (token == null) {  // 通过url参数传递token
            token = httpRequest.getParameter("token");
        }
        // 判断是否有值
        if (StringUtils.isNoneBlank(token)) {
            // 设置当前session状态
            servletRequest.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "url");
            servletRequest.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
            servletRequest.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);

            try {
                Claims claims = jwtUtils.parseJWT(token);
                JSONObject json = JSONObject.parseObject(claims.getSubject());

                String sessionId = json.getString("sessionId");
                logger.info("Reuest {} get sessionId from request header {}", httpRequest.getRequestURI(), sessionId);
                return sessionId;

            } catch (Exception e) {
                logger.error("Get session id by parse token  error.  url: {}", httpRequest.getRequestURI());
                e.printStackTrace();
            }
            logger.info("Request {} get tokenId from request header {}", httpRequest.getRequestURI(), token);
            return token;
        }
        // 若header获取不到token则尝试从cookie中获取
        // Serializable serializable = super.getSessionId(servletRequest, servletResponse);
        Serializable serializable = UUID.randomUUID();  // 支持浏览器两个页面用不同的用户登录
        logger.info("Request {} get sessionId from super get getSessionId {}", httpRequest.getRequestURI(), serializable.toString());
        return serializable;
    }

    /**
     * 获取所有活动中的Session
     * @return
     */
    public Collection<Session> getActiveSessions() {
        Collection<Session> sessionList = super.getActiveSessions();
        return sessionList;
    }
}