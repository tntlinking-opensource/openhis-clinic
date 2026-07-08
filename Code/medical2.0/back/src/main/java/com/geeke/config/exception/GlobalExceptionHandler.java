package com.geeke.config.exception;

import com.alibaba.fastjson.JSONObject;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.Constants;
import com.geeke.utils.constants.ErrorEnum;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一异常拦截
 * @author: hxy
 * @date: 2017/10/24 10:31
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 构建统一错误响应
     */
    private JSONObject buildErrorResponse(String code, String msg, String msgType) {
        JSONObject json = new JSONObject();
        json.put(Constants.RETURN_CODE, code);
        json.put(Constants.RETURN_MSG, msg);
        json.put(Constants.RETURN_MSG_TYPE, msgType);
        json.put(Constants.RETURN_DATA, new JSONObject());
        return json;
    }

    /**
     * 通用异常拦截 — 不向客户端暴露堆栈信息
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<JSONObject> defaultErrorHandler(HttpServletRequest req, Exception e) {
        logger.error("请求异常: {} {}", req.getMethod(), req.getRequestURI(), e);
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), ErrorEnum.E_400.getErrorMsg(), Constants.MSG_TYPE_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 业务逻辑异常拦截 — 返回具体业务错误信息给前端
     * 用于 Service 层抛出的业务校验异常
     */
    @ExceptionHandler(com.geeke.common.service.ServiceException.class)
    public ResponseEntity<JSONObject> serviceExceptionHandler(com.geeke.common.service.ServiceException e) {
        logger.warn("业务异常: {}", e.getMessage());
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), e.getMessage(), Constants.MSG_TYPE_WARNING);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * RuntimeException 拦截 — 返回错误信息，不暴露堆栈
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<JSONObject> runtimeExceptionHandler(RuntimeException e) {
        logger.error("运行时异常: {}", e.getMessage(), e);
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), "系统内部错误", Constants.MSG_TYPE_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * @Valid 参数校验失败拦截
     * 当 @RequestBody 上的实体字段不满足 @NotNull 等约束时触发
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JSONObject> validationExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        logger.warn("参数校验失败: {}", message);
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), message, Constants.MSG_TYPE_WARNING);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * GET/POST请求方法错误的拦截器
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<JSONObject> httpRequestMethodHandler(HttpRequestMethodNotSupportedException e) {
        logger.warn("请求方法不支持: {}", e.getMessage());
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), "请求方法不支持: " + e.getMethod(), Constants.MSG_TYPE_ERROR);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(result);
    }

    /**
     * 权限不足报错拦截
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<JSONObject> unauthorizedExceptionHandler() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResultUtil.errorJson(ErrorEnum.E_502));
    }

    /**
     * 请求体JSON格式错误拦截
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<JSONObject> httpMessageNotReadableHandler(HttpMessageNotReadableException e) {
        logger.warn("请求体格式错误: {}", e.getMessage());
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), "请求体格式错误，请检查JSON格式", Constants.MSG_TYPE_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 缺少请求参数拦截
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<JSONObject> missingParamHandler(MissingServletRequestParameterException e) {
        logger.warn("缺少请求参数: {}", e.getMessage());
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), "缺少必填参数: " + e.getParameterName(), Constants.MSG_TYPE_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 404路径不存在拦截
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<JSONObject> noHandlerFoundHandler(NoHandlerFoundException e) {
        logger.warn("路径不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        JSONObject result = buildErrorResponse(ErrorEnum.E_400.getErrorCode(), "请求路径不存在", Constants.MSG_TYPE_ERROR);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 未登录报错拦截
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<JSONObject> unauthenticatedException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResultUtil.errorJson(ErrorEnum.E_20011));
    }
}
