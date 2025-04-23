package com.platform.common.exception;

import lombok.Getter;

/**
 * Business exception for all business logic related errors.
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code
     */
    private final Integer code;

    /**
     * Create a new business exception with default error code 400
     *
     * @param message Error message
     */
    public BusinessException(String message) {
        this(400, message);
    }

    /**
     * Create a new business exception with a specific error code
     *
     * @param code Error code
     * @param message Error message
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Create a new business exception with a specific error code and cause
     *
     * @param code Error code
     * @param message Error message
     * @param cause The cause of this exception
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
