package com.example.common.exception;

import com.example.common.constant.SystemConstants;
import lombok.Getter;

/**
 * 平台基础异常
 *
 * @author platform
 * @since 1.0.0
 */
@Getter
public class PlatformException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public PlatformException(String message) {
        this(SystemConstants.ERROR_CODE, message);
    }

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public PlatformException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   异常
     */
    public PlatformException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}