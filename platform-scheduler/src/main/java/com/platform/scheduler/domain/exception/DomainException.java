package com.platform.scheduler.domain.exception;

/**
 * 领域异常基类
 * 表示在领域模型中发生的所有业务相关异常的基类
 *
 * @author platform
 */
public class DomainException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误代码
     */
    private final String errorCode;
    
    /**
     * 构造方法
     *
     * @param message 异常消息
     */
    public DomainException(String message) {
        this("DOMAIN_ERROR", message);
    }
    
    /**
     * 构造方法
     *
     * @param errorCode 错误代码
     * @param message 异常消息
     */
    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造方法
     *
     * @param errorCode 错误代码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public DomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
}
