package com.platform.common.exception;

import com.platform.common.model.ResultCode;

import lombok.Getter;

/**
 * 业务异常基类
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private final ResultCode resultCode;
    
    /**
     * 错误消息
     */
    private final String message;
    
    /**
     * 错误详情数据
     */
    private final transient Object errorData;
    
    /**
     * 基于错误码构造业务异常
     * 
     * @param resultCode 错误码
     */
    public BusinessException(ResultCode resultCode) {
        this(resultCode, resultCode.getMessage(), null);
    }
    
    /**
     * 基于错误码与自定义消息构造业务异常
     * 
     * @param resultCode 错误码
     * @param message 自定义消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        this(resultCode, message, null);
    }
    
    /**
     * 基于错误码、自定义消息与详情数据构造业务异常
     * 
     * @param resultCode 错误码
     * @param message 自定义消息
     * @param errorData 详情数据
     */
    public BusinessException(ResultCode resultCode, String message, Object errorData) {
        super(message);
        this.resultCode = resultCode;
        this.message = message;
        this.errorData = errorData;
    }
    
    /**
     * 基于错误码与原始异常构造业务异常
     * 
     * @param resultCode 错误码
     * @param cause 原始异常
     */
    public BusinessException(ResultCode resultCode, Throwable cause) {
        this(resultCode, resultCode.getMessage(), null, cause);
    }
    
    /**
     * 基于错误码、自定义消息与原始异常构造业务异常
     * 
     * @param resultCode 错误码
     * @param message 自定义消息
     * @param cause 原始异常
     */
    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        this(resultCode, message, null, cause);
    }
    
    /**
     * 基于错误码、自定义消息、详情数据与原始异常构造业务异常
     * 
     * @param resultCode 错误码
     * @param message 自定义消息
     * @param errorData 详情数据
     * @param cause 原始异常
     */
    public BusinessException(ResultCode resultCode, String message, Object errorData, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
        this.message = message;
        this.errorData = errorData;
    }
}
