package com.geeke.utils;

import com.alibaba.fastjson.JSONObject;
import com.geeke.utils.constants.Constants;
import com.geeke.utils.constants.ErrorEnum;

/**
 * @author: lys
 * @description: 后台服务返回值处理工具类
 * @date: 2010/10/24 10:12
 */
public class ResultUtil {

    /**
     * 返回一个returnData为空对象的成功消息的json
     *
     * @return
     */
    public static JSONObject successJson() {
        return successJson(new JSONObject());
    }

    /**
     * 返回一个返回码为100的json
     *
     * @param returnData json里的主要内容
     * @return
     */
    public static JSONObject successJson(Object returnData) {
        return successJson(Constants.SUCCESS_MSG, returnData);
    }

    /**
     * 草稿保存成功时返回信息
     * @param returnData
     * @return
     */
    public static JSONObject successSaveDraft(Object returnData) {
        return successSaveDraft(Constants.SUCCESS_SAVE_DRAFT, returnData);
    }

    /**
     * 返回一个返回码为100的json
     * @param msg
     * @param returnData
     * @return
     */
    public static JSONObject successSaveDraft(String msg, Object returnData) {
        JSONObject resultJson = new JSONObject();
        resultJson.put(Constants.RETURN_MSG_TYPE, Constants.MSG_TYPE_SUCCESS);
        resultJson.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
        resultJson.put(Constants.RETURN_MSG, msg);
        resultJson.put(Constants.RETURN_DATA, returnData);
        return resultJson;
    }
    /**
     * 返回一个返回码为100的json
     * @param msg 信息
     * @param returnData json里的主要内容
     * @return
     */
    public static JSONObject successJson(String msg, Object returnData) {
        JSONObject resultJson = new JSONObject();
        resultJson.put(Constants.RETURN_MSG_TYPE, Constants.MSG_TYPE_SUCCESS);
        resultJson.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
        resultJson.put(Constants.RETURN_MSG, msg);
        resultJson.put(Constants.RETURN_DATA, returnData);
        return resultJson;
    } 
    
    /**
     * 返回警告信息JSON
     *
     * @param errorEnum 错误码的errorEnum
     * @return
     */
    public static JSONObject warningJson(ErrorEnum errorEnum) {
        JSONObject resultJson = new JSONObject();
        resultJson.put(Constants.RETURN_MSG_TYPE, Constants.MSG_TYPE_WARNING);
        resultJson.put(Constants.RETURN_CODE, errorEnum.getErrorCode());
        resultJson.put(Constants.RETURN_MSG, errorEnum.getErrorMsg());
        return resultJson;
    }    

    /**
     * 返回警告信息JSON
     * @param errorEnum 错误码的errorEnum
     * @param msg 自定义的信息
     * @return
     */
    public static JSONObject warningJson(ErrorEnum errorEnum, String msg) {
        JSONObject resultJson = new JSONObject();
        resultJson.put(Constants.RETURN_MSG_TYPE, Constants.MSG_TYPE_WARNING);
        resultJson.put(Constants.RETURN_CODE, errorEnum.getErrorCode());
        resultJson.put(Constants.RETURN_MSG, msg);
        return resultJson;
    }     
    
    /**
     * 返回错误信息JSON
     *
     * @param errorEnum 错误码的errorEnum
     * @return
     */
    public static JSONObject errorJson(ErrorEnum errorEnum) {
        JSONObject resultJson = new JSONObject();
        resultJson.put(Constants.RETURN_MSG_TYPE, Constants.MSG_TYPE_ERROR);
        resultJson.put(Constants.RETURN_CODE, errorEnum.getErrorCode());
        resultJson.put(Constants.RETURN_MSG, errorEnum.getErrorMsg());
        return resultJson;
    }
    
    /**
     * 返回错误信息JSON
     * @param errorEnum 错误码的errorEnum
     * @param msg 自定义的信息
     * @return
     */
    public static JSONObject errorJson(ErrorEnum errorEnum, String msg) {
        JSONObject resultJson = new JSONObject();
        resultJson.put(Constants.RETURN_MSG_TYPE, Constants.MSG_TYPE_ERROR);
        resultJson.put(Constants.RETURN_CODE, errorEnum.getErrorCode());
        resultJson.put(Constants.RETURN_MSG, msg);
        return resultJson;
    }    
}
