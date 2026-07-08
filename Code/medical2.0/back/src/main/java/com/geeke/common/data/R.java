package com.geeke.common.data;

import com.geeke.utils.constants.Constants;
import com.geeke.utils.constants.ErrorEnum;

/**
 * 统一响应体泛型封装
 * 逐步替代 ResultUtil 返回的 JSONObject，提供类型安全的响应结构。
 *
 * <p>使用示例:
 * <pre>
 *   // 成功
 *   return ResponseEntity.ok(R.ok(entity));
 *   return ResponseEntity.ok(R.ok("保存成功", id));
 *
 *   // 业务警告（表单校验失败等）
 *   return ResponseEntity.ok(R.warning(ErrorEnum.E_50001));
 *
 *   // 系统错误
 *   return ResponseEntity.ok(R.error(ErrorEnum.E_400));
 * </pre>
 *
 * @param <T> 响应数据类型
 */
public class R<T> {

    private String code;
    private String msg;
    private String type;
    private T data;

    public R() {
    }

    public R(String code, String msg, String type, T data) {
        this.code = code;
        this.msg = msg;
        this.type = type;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    /**
     * 成功（无数据）
     */
    public static <T> R<T> ok() {
        return new R<>(Constants.SUCCESS_CODE, Constants.SUCCESS_MSG, Constants.MSG_TYPE_SUCCESS, null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(Constants.SUCCESS_CODE, Constants.SUCCESS_MSG, Constants.MSG_TYPE_SUCCESS, data);
    }

    /**
     * 成功（自定义消息 + 数据）
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(Constants.SUCCESS_CODE, msg, Constants.MSG_TYPE_SUCCESS, data);
    }

    /**
     * 草稿保存成功
     */
    public static <T> R<T> okDraft(T data) {
        return new R<>(Constants.SUCCESS_CODE, Constants.SUCCESS_SAVE_DRAFT, Constants.MSG_TYPE_SUCCESS, data);
    }

    // ==================== 警告响应 ====================

    /**
     * 业务警告（使用ErrorEnum默认消息）
     */
    public static <T> R<T> warning(ErrorEnum errorEnum) {
        return new R<>(String.valueOf(errorEnum.getErrorCode()), errorEnum.getErrorMsg(), Constants.MSG_TYPE_WARNING, null);
    }

    /**
     * 业务警告（自定义消息）
     */
    public static <T> R<T> warning(ErrorEnum errorEnum, String msg) {
        return new R<>(String.valueOf(errorEnum.getErrorCode()), msg, Constants.MSG_TYPE_WARNING, null);
    }

    // ==================== 错误响应 ====================

    /**
     * 系统错误（使用ErrorEnum默认消息）
     */
    public static <T> R<T> error(ErrorEnum errorEnum) {
        return new R<>(String.valueOf(errorEnum.getErrorCode()), errorEnum.getErrorMsg(), Constants.MSG_TYPE_ERROR, null);
    }

    /**
     * 系统错误（自定义消息）
     */
    public static <T> R<T> error(ErrorEnum errorEnum, String msg) {
        return new R<>(String.valueOf(errorEnum.getErrorCode()), msg, Constants.MSG_TYPE_ERROR, null);
    }

    // ==================== Getter/Setter ====================

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
