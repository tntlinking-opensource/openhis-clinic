package com.geeke.config.shiro;

import io.jsonwebtoken.Claims;

import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.IUser;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.JwtUtils;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;

/**
 * @author: lys
 * @description: 对没有登录的请求进行拦截, 全部返回json信息. 覆盖掉shiro原本的跳转login.jsp的拦截方式
 * @date: 2017/10/24 10:11
 */
@Component
public class AjaxPermissionsAuthorizationFilter extends FormAuthenticationFilter {
    private final static Logger logger = LoggerFactory.getLogger(AjaxPermissionsAuthorizationFilter.class);
    
	private JwtUtils jwtUtils;

	public AjaxPermissionsAuthorizationFilter(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}
	
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
    	HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String token = httpRequest.getHeader("X-Token");

		// 注意：通过URL参数传递token存在安全风险（token会出现在访问日志、浏览器历史中）
		// 仅在无法设置请求头的场景（如文件下载）中使用
		if( token == null ) {
			token = httpRequest.getParameter("token");
		}

    	if(token == null ) {
    		logger.info("Can not get token from the hearder of {} ", httpRequest.getRequestURI());
    		return false;
    	}
    	try {

	    	Claims claims = jwtUtils.parseJWT(token);
	    	// JWT过期验证已通过JwtUtils中的setExpiration实现
	    	String host = request.getRemoteHost();
			JSONObject json = JSONObject.parseObject(claims.getSubject());
			if(!host.equals(json.getString("host"))) {
				logger.info("The host of the token is inconsistent. url: {} ", httpRequest.getRequestURI());
				return false;
			}

			// 检查用户Id
			IUser user = SessionUtils.getUser();
			if(user == null) {
				logger.info("Session is expired. {}", httpRequest.getRequestURI());
				return false;
			}
			if(user.getId() == null || !user.getId().equals(json.getString("userId"))) {
				logger.info("The user of the token is inconsistent. url: {} ", httpRequest.getRequestURI());
				return false;
			}
    	}catch(Exception e){
    		logger.error("Access allowed error.  url: {}", httpRequest.getRequestURI(), e);
    		return false;
    	}
        return true;
    }
	
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        JSONObject jsonObject = ResultUtil.errorJson(ErrorEnum.E_20011);
        PrintWriter out = null;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            res.setCharacterEncoding("UTF-8");
            res.setContentType("application/json");
            out = response.getWriter();
            out.println(jsonObject);
        } catch (Exception e) {
            logger.debug("Response write error ignored", e);
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
        return false;
    }
}
