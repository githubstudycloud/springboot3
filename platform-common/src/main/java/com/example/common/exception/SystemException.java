package com.example.common.exception;

import com.example.common.constant.SystemConstants;

/**
 * 系统异常
 * 用于表示系统级别的异常，通常会记录堆栈信息
 *
 * @author platform
 * @since 1.0.0
 */
public class SystemException extends PlatformException {
    private static final long serialVersionUID = 1L;
    
    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public SystemException(String message) {
        super(SystemConstants.ERROR_CODE, message);
    }
    
    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public SystemException(Integer code, String message) {
        super(code, message);
    }
    
    /**
     * 构造函数
     *
     * @param message 错误消息
     * @param cause   异常
     */
    public SystemException(String message, Throwable cause) {
        super(SystemConstants.ERROR_CODE, message, cause);
    }
    
    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   异常
     */
    public SystemException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
    /**
     * 服务不可用异常
     *
     * @param message 错误消息
     * @return 系统异常
     */
    public static SystemException serviceUnavailable(String message) {
        return new SystemException(SystemConstants.SERVICE_UNAVAILABLE_CODE, message);
    }
}