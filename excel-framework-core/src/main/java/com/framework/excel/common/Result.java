package com.framework.excel.common;

import java.io.Serializable;

/**
 * 统一响应结果类
 *
 * @author Framework
 * @since 1.0.0
 */
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;
    
    /**
     * 失败状态码
     */
    public static final int FAILED_CODE = 500;
    
    /**
     * 参数错误状态码
     */
    public static final int PARAM_ERROR_CODE = 400;
    
    /**
     * 状态码
     */
    private int code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功响应
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, "操作成功", null);
    }
    
    /**
     * 成功响应（带数据）
     *
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, "操作成功", data);
    }
    
    /**
     * 成功响应（带消息和数据）
     *
     * @param message 消息
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }
    
    /**
     * 失败响应
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> failed() {
        return new Result<>(FAILED_CODE, "操作失败", null);
    }
    
    /**
     * 失败响应（带消息）
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(FAILED_CODE, message, null);
    }
    
    /**
     * 失败响应（带状态码和消息）
     *
     * @param code 状态码
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> failed(int code, String message) {
        return new Result<>(code, message, null);
    }
    
    /**
     * 参数错误响应
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> Result<T> paramError(String message) {
        return new Result<>(PARAM_ERROR_CODE, message, null);
    }
    
    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }
    
    // Getters and setters
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
