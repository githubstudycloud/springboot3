package com.example.common.exception;

import com.example.common.constant.SystemConstants;

/**
 * 业务异常
 * 用于表示业务逻辑相关的异常，通常不会记录堆栈信息
 *
 * @author platform
 * @since 1.0.0
 */
public class BusinessException extends PlatformException {
    private static final long serialVersionUID = 1L;
    
    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(SystemConstants.BAD_REQUEST_CODE, message);
    }
    
    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(code, message);
    }
    
    /**
     * 未找到资源异常
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(SystemConstants.NOT_FOUND_CODE, message);
    }
    
    /**
     * 未授权异常
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(SystemConstants.UNAUTHORIZED_CODE, message);
    }
    
    /**
     * 禁止访问异常
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(SystemConstants.FORBIDDEN_CODE, message);
    }
    
    /**
     * 参数错误异常
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(SystemConstants.BAD_REQUEST_CODE, message);
    }
}