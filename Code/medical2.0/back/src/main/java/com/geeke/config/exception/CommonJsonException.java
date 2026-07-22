package com.geeke.config.exception;

import com.alibaba.fastjson.JSONObject;

/**
 * 通用json异常类
 * 
 * @author hxy
 * @date 2017/11/15
 */
public class CommonJsonException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private JSONObject resultJson;

	public CommonJsonException(JSONObject resultJson) {
		super(resultJson.toString());
		this.resultJson = resultJson;
	}

	public JSONObject getResultJson() {
		return resultJson;
	}

}
