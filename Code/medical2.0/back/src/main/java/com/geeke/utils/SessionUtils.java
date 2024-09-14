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
		return getUserJson().toJavaObject(User.class);
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
		SecurityUtils.getSubject().getSession().setAttribute("tenantID", tenantId);
	}

	/**
	 * 设置当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static void setLoginTenant(Company company){
		JSONObject com = (JSONObject) JSONObject.toJSON(company);
		SecurityUtils.getSubject().getSession().setAttribute("tenant", com);
	}

	/**
	 * 获取当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static String getLoginTenantId(){
		return String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("tenantID"));
	}

	/**
	 * 设置当前用户所登录的诊所
	 * @return 取不到返回 new User()
	 */
	public static Company getLoginTenant(){
		JSONObject j = (JSONObject)SecurityUtils.getSubject().getSession().getAttribute("tenant");
		return j.toJavaObject(Company.class);
	}

}
