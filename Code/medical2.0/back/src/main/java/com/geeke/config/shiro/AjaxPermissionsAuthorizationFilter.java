package com.geeke.config.shiro;

import io.jsonwebtoken.Claims;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

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
		String userId = httpRequest.getHeader("User-Id");
		List<String> userIdSet=new ArrayList<>();
		//添加拥有公众号的诊所id
		userIdSet.add("998324809623052308");
		userIdSet.add("1107842936624949644");

		if( token == null ) {  // 通过url参数传递token
			token = httpRequest.getParameter("token");
		}
		
    	if(token == null ) {
    		logger.info("Can not get token from the hearder of {} ", httpRequest.getRequestURI());
    		return false;
    	}
    	try {

	    	Claims claims = jwtUtils.parseJWT(token);
/*	    	if((new Date()).after(claims.getExpiration())) {
	    		logger.info("Expired token for {} ", httpRequest.getRequestURI());
	    		
	    		SecurityUtils.getSubject().logout();
	    		return false;
	    	}*/
	    	String host = request.getRemoteHost();
			JSONObject json = JSONObject.parseObject(claims.getSubject());
			if(!host.equals(json.getString("host"))) {
				logger.info("The host of the token is inconsistent. url: {} ", httpRequest.getRequestURI());
				return false;
			}

			//用于公众号校验功能
			for (String id:
				 userIdSet) {
				if(id.equals(userId)){
					return true;
				}
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
    		logger.error("Access allowed error.  url: {}", httpRequest.getRequestURI());
    		e.printStackTrace();
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
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
        return false;
    }

    @Bean
    public FilterRegistrationBean<AjaxPermissionsAuthorizationFilter> registration(AjaxPermissionsAuthorizationFilter filter) {
        FilterRegistrationBean<AjaxPermissionsAuthorizationFilter> registration = new FilterRegistrationBean<AjaxPermissionsAuthorizationFilter>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
