package com.geeke.config.exception;

import com.alibaba.fastjson.JSONObject;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.Constants;
import com.geeke.utils.constants.ErrorEnum;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: hxy
 * @description: 统一异常拦截
 * @date: 2017/10/24 10:31
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger("GlobalExceptionHandler");

    @ExceptionHandler(value = Exception.class)
    public JSONObject defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        String errorPosition = "";
        //如果错误堆栈信息存在
        if (e.getStackTrace().length > 0) {
            StackTraceElement element = e.getStackTrace()[0];
            String fileName = element.getFileName() == null ? "未找到错误文件" : element.getFileName();
            int lineNumber = element.getLineNumber();
            errorPosition = fileName + ":" + lineNumber;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.RETURN_CODE, ErrorEnum.E_400.getErrorCode());
        jsonObject.put(Constants.RETURN_MSG, ErrorEnum.E_400.getErrorMsg());
        jsonObject.put(Constants.RETURN_MSG_TYPE, Constants.MSG_TYPE_ERROR);
        JSONObject errorObject = new JSONObject();
        errorObject.put("errorLocation", e.toString() + "    错误位置:" + errorPosition);
        jsonObject.put(Constants.RETURN_DATA, errorObject);
        logger.error("异常", e);
        return jsonObject;
    }

    /**
     * GET/POST请求方法错误的拦截器
     * 因为开发时可能比较常见,而且发生在进入controller之前,上面的拦截器拦截不到这个错误
     * 所以定义了这个拦截器
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public JSONObject httpRequestMethodHandler() throws Exception {
        return ResultUtil.errorJson(ErrorEnum.E_500);
    }

    /**
     * 本系统自定义错误的拦截器
     * 拦截到此错误之后,就返回这个类里面的json给前端
     * 常见使用场景是参数校验失败,抛出此错,返回错误信息给前端
     *
     * @param commonJsonException
     * @return
     * @throws Exception
     */
    @ExceptionHandler(CommonJsonException.class)
    public JSONObject commonJsonExceptionHandler(CommonJsonException commonJsonException) throws Exception {
        return commonJsonException.getResultJson();
    }

    /**
     * 权限不足报错拦截
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(UnauthorizedException.class)
    public JSONObject unauthorizedExceptionHandler() throws Exception {
        return ResultUtil.errorJson(ErrorEnum.E_502);
    }

    /**
     * 未登录报错拦截
     * 在请求需要权限的接口,而连登录都还没登录的时候,会报此错
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public JSONObject unauthenticatedException() throws Exception {
        return ResultUtil.errorJson(ErrorEnum.E_20011);
    }
    
//    /**
//     * 流程引擎异常拦截
//     * @param processEngineException
//     * @return
//     * @throws Exception
//     */
//    @ExceptionHandler(ProcessEngineException.class)
//    public JSONObject processEngineExceptionHandler(ProcessEngineException processEngineException) throws Exception {
//    	return ResultUtil.errorJson(ErrorEnum.E_30001, processEngineException.getMessage());
//    }
//
//    /**
//     * 流程引擎警告拦截
//     * @param processEngineWarning
//     * @return
//     * @throws Exception
//     */
//    @ExceptionHandler(ProcessEngineWarning.class)
//    public JSONObject processEngineWarningHandler(ProcessEngineWarning processEngineWarning) throws Exception {
//    	return ResultUtil.warningJson(ErrorEnum.E_30002, processEngineWarning.getMessage());
//    }
}
