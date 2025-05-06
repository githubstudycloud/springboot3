package com.platform.visualization.interfaces.rest.response;

import java.time.LocalDateTime;

/**
 * API通用响应对象
 */
public class ApiResponse<T> {
    
    /**
     * 响应码
     */
    private String code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间
     */
    private LocalDateTime timestamp;
    
    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("200", "操作成功", data, LocalDateTime.now());
    }
    
    /**
     * 创建成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("200", "操作成功", null, LocalDateTime.now());
    }
    
    /**
     * 创建失败响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null, LocalDateTime.now());
    }
    
    /**
     * 创建参数错误响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>("400", message, null, LocalDateTime.now());
    }
    
    /**
     * 创建未找到资源响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>("404", message, null, LocalDateTime.now());
    }
    
    /**
     * 创建服务器错误响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>("500", message, null, LocalDateTime.now());
    }
    
    // 构造函数
    private ApiResponse(String code, String message, T data, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }
    
    // Getter方法
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public T getData() {
        return data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}