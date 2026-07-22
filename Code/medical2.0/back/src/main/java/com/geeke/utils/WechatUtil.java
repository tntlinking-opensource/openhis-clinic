package com.geeke.utils;



import com.alibaba.fastjson.JSONObject;
// vo实体类参数

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j(topic = "WechatUtils")
@Component
public class WechatUtil {
	private static final String WECHAT_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";
	private static final String WECHAT_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}";
	private static final String WECHAT_PHONE_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";

	@Value("${wechat.app-id:}")
	private String appId;

	@Value("${wechat.secret:}")
	private String secret;

	private final RestTemplate restTemplate;

	public WechatUtil(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * 获取小程序code换取openid、session_key
	 */
	public JSONObject getOpenId(String code) {
		String url = WECHAT_SESSION_URL;
		try {
			Map<String, String> uriVars = new HashMap<>();
			uriVars.put("appid", appId);
			uriVars.put("secret", secret);
			uriVars.put("code", code);
			String response = restTemplate.getForObject(url, String.class, uriVars);
			return JSONObject.parseObject(response);
		} catch (Exception e) {
			log.error("微信请求失败", e);
		}
		return null;
	}

	/**
	 * 获取access_token
	 */
	public JSONObject getToken() {
		String url = WECHAT_TOKEN_URL;
		try {
			Map<String, String> uriVars = new HashMap<>();
			uriVars.put("appid", appId);
			uriVars.put("secret", secret);
			String response = restTemplate.getForObject(url, String.class, uriVars);
			return JSONObject.parseObject(response);
		} catch (Exception e) {
			log.error("微信请求失败", e);
		}
		return null;
	}

	/**
	 * 获取手机号
	 */
	public JSONObject getPhoneNumber(String code, String token) {
		String url = WECHAT_PHONE_URL + token;
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("code", code);
			String response = restTemplate.postForObject(url, data, String.class);
			return JSONObject.parseObject(response);
		} catch (Exception e) {
			log.error("微信获取手机号失败", e);
		}
		return null;
	}
}
