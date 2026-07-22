package com.geeke.utils;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.org.entity.Company;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 用户工具类
 * @author lys
 * @version 2018-12-05
 */
public class SessionUtils extends com.geeke.sys.utils.SessionUtils {
	private static final Logger logger = LoggerFactory.getLogger(SessionUtils.class);

	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser(){
		JSONObject userJson = getUserJson();
		if (userJson == null) {
			return null;
		}
		return userJson.toJavaObject(User.class);
	}
	
	
    /**
	 * 设置当前用户
	 * @return 取不到返回 new User()
	 */
	public static void setUser(User user){
		setUserJson((JSONObject)JSONObject.toJSON(user));
	}

	/**
	 * 设置当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static void setLoginTenantId(String tenantId){
		try {
			SecurityUtils.getSubject().getSession().setAttribute("tenantID", tenantId);
		} catch (Exception e) {
			logger.debug("Session 已过期或不存在", e);
		}
	}

	/**
	 * 设置当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static void setLoginTenant(Company company){
		try {
			JSONObject com = (JSONObject) JSONObject.toJSON(company);
			SecurityUtils.getSubject().getSession().setAttribute("tenant", com);
		} catch (Exception e) {
			logger.debug("Session 已过期或不存在", e);
		}
	}

	/**
	 * 获取当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static String getLoginTenantId(){
		try {
			Object attr = SecurityUtils.getSubject().getSession().getAttribute("tenantID");
			return attr == null ? null : String.valueOf(attr);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static Company getLoginTenant(){
		try {
			JSONObject j = (JSONObject)SecurityUtils.getSubject().getSession().getAttribute("tenant");
			return j.toJavaObject(Company.class);
		} catch (Exception e) {
			return null;
		}
	}

}
