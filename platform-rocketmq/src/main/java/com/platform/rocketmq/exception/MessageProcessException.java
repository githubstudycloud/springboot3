package com.platform.rocketmq.exception;

/**
 * 消息处理异常
 * 用于封装消息处理过程中的所有异常
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class MessageProcessException extends Exception {
    
    /**
     * 错误码
     */
    private final String errorCode;
    
    /**
     * 是否需要重试
     */
    private final boolean needRetry;
    
    /**
     * 构造方法
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @param needRetry 是否需要重试
     */
    public MessageProcessException(String errorCode, String message, boolean needRetry) {
        super(message);
        this.errorCode = errorCode;
        this.needRetry = needRetry;
    }
    
    /**
     * 构造方法（带原因）
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @param needRetry 是否需要重试
     * @param cause 原因
     */
    public MessageProcessException(String errorCode, String message, boolean needRetry, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.needRetry = needRetry;
    }
    
    /**
     * 创建需要重试的异常
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @return 异常实例
     */
    public static MessageProcessException retryable(String errorCode, String message) {
        return new MessageProcessException(errorCode, message, true);
    }
    
    /**
     * 创建需要重试的异常（带原因）
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @param cause 原因
     * @return 异常实例
     */
    public static MessageProcessException retryable(String errorCode, String message, Throwable cause) {
        return new MessageProcessException(errorCode, message, true, cause);
    }
    
    /**
     * 创建不需要重试的异常
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @return 异常实例
     */
    public static MessageProcessException nonRetryable(String errorCode, String message) {
        return new MessageProcessException(errorCode, message, false);
    }
    
    /**
     * 创建不需要重试的异常（带原因）
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @param cause 原因
     * @return 异常实例
     */
    public static MessageProcessException nonRetryable(String errorCode, String message, Throwable cause) {
        return new MessageProcessException(errorCode, message, false, cause);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public boolean isNeedRetry() {
        return needRetry;
    }
}