package com.geeke.utils;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.org.entity.Company;
import org.apache.shiro.SecurityUtils;


/**
 * 用户工具类
 * @author lys
 * @version 2018-12-05
 */
public class SessionUtils extends com.geeke.sys.utils.SessionUtils {
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
			// Session 已过期或不存在
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
			// Session 已过期或不存在
		}
	}

	/**
	 * 获取当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static String getLoginTenantId(){
		try {
			return String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("tenantID"));
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
