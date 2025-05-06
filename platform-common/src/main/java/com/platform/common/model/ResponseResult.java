package com.platform.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

/**
 * 统一API响应结果封装
 *
 * @param <T> 响应数据类型
 */
@Data
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private String timestamp;
    
    /**
     * 请求ID，用于跟踪请求
     */
    private String requestId;
    
    /**
     * 默认私有构造函数
     */
    private ResponseResult() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @return 成功响应
     */
    public static <T> ResponseResult<T> success() {
        return success(null);
    }
    
    /**
     * 成功响应（有数据）
     * 
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }
    
    /**
     * 失败响应
     * 
     * @param resultCode 错误码
     * @return 失败响应
     */
    public static <T> ResponseResult<T> failure(ResultCode resultCode) {
        return failure(resultCode, resultCode.getMessage());
    }
    
    /**
     * 失败响应（自定义消息）
     * 
     * @param resultCode 错误码
     * @param message 错误消息
     * @return 失败响应
     */
    public static <T> ResponseResult<T> failure(ResultCode resultCode, String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败响应（自定义消息和数据）
     * 
     * @param resultCode 错误码
     * @param message 错误消息
     * @param data 响应数据
     * @return 失败响应
     */
    public static <T> ResponseResult<T> failure(ResultCode resultCode, String message, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    /**
     * 设置请求ID
     * 
     * @param requestId 请求ID
     * @return 当前对象
     */
    public ResponseResult<T> setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
