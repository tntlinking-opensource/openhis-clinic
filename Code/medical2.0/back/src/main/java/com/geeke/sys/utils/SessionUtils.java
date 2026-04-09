package com.geeke.sys.utils;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import java.util.List;


/**
 * 用户工具类
 * @author lys
 * @version 2018-12-05
 */
public class SessionUtils {
    /**
     * session中存放用户信息的key值
     */
    private static final String SESSION_USER_INFO = "userInfo";
    private static final String SESSION_USER_PERMISSION = "userPermission";
	private static final String SESSION_YEWECHAT_OPENID = "openId";

	
	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static JSONObject getUserJson(){
		try {
			return (JSONObject)SecurityUtils.getSubject().getSession().getAttribute(SESSION_USER_INFO);
		} catch (Exception e) {
			return null;
		}
	}


    /**
     * 获取当前用户
     *
     * @return 取不到返回 new User()
     */
    public static SessionUserDto getUserDto() {
        try {
            Object object = SecurityUtils.getSubject().getSession().getAttribute(SESSION_USER_INFO);
            return BeanUtil.copyProperties(object, SessionUserDto.class);
        } catch (Exception e) {
            return null;
        }
    }

	/**
	 * 从指定Session获取用户信息
	 * @param session
	 * @return
	 */
	public static JSONObject getUserJson(Session session){
		if (session == null) {
			return null;
		}
		return (JSONObject)session.getAttribute(SESSION_USER_INFO);
	}
	
	
    /**
	 * 设置当前用户
	 * @return 取不到返回 new User()
	 */
	public static void setUserJson(JSONObject userObj ){
		try {
			SecurityUtils.getSubject().getSession().setAttribute(SESSION_USER_INFO, userObj);
		} catch (Exception e) {
			// Session 已过期或不存在
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getUserPermission() {
		try {
			return (List<String>)SecurityUtils.getSubject().getSession().getAttribute(SESSION_USER_PERMISSION);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void setUserPermission(List<String> permissionList) {
		try {
			SecurityUtils.getSubject().getSession().setAttribute(SESSION_USER_PERMISSION, permissionList);
		} catch (Exception e) {
			// Session 已过期或不存在
		}
	}


	/**
	 * 获取企业微信当前用户
	 * @return 取不到返回 JSONObject
	 */
	public static JSONObject getWeChatUser(){
		try {
			return (JSONObject)SecurityUtils.getSubject().getSession().getAttribute(SESSION_YEWECHAT_OPENID);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 设置企业微信当前用户
	 * @return
	 */
	public static void setWeChatUser(JSONObject jsonObject){
		try {
			SecurityUtils.getSubject().getSession().setAttribute(SESSION_YEWECHAT_OPENID, jsonObject);
		} catch (Exception e) {
			// Session 已过期或不存在
		}
	}
}
