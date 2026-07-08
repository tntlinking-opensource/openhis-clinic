package com.geeke.sys.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;

import com.alibaba.fastjson.JSONObject;
import com.geeke.utils.JwtUtils;

/**
 * Controller基类
 * @author lys
 * @date 2017/11/15
 */
public class BaseController {
	
	/**
	 * 日志对象
	 */
	protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	/**
	 * 从jwt中获取subject信息
	 * @param jwtUtils
	 * @param jwt
	 * @param key
	 * @return
	 */
	protected String getSubjectFromJwt(JwtUtils jwtUtils, String jwt, String key) {
		try {
			Claims claims = jwtUtils.parseJWT(jwt);
			String subject = claims.getSubject();
			if(key != null && !key.trim().equals("")) {
				JSONObject json = JSONObject.parseObject(subject);
				return json.getString(key);
			} else {
				return subject;
			}
		} catch (Exception e) {
			logger.warn("Failed to parse JWT: {}", e.getMessage());
			return null;
		}
	}

}
