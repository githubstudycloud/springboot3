package com.example.common.exception;

/**
 * 基础异常类
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    public BaseException(String message) {
        this("500", message);
    }

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 