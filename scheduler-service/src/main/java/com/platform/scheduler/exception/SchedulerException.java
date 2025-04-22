package com.platform.scheduler.exception;

/**
 * 调度器异常基类
 * 
 * @author platform
 */
public class SchedulerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 构造函数
     */
    public SchedulerException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public SchedulerException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     * @param cause 异常原因
     */
    public SchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param cause 异常原因
     */
    public SchedulerException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message 错误消息
     */
    public SchedulerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public SchedulerException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 设置错误码
     *
     * @param errorCode 错误码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
