package org.example.platform.common.exception;

import lombok.Getter;

/**
 * Business Exception
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    
    /**
     * Constructor with message and default code 500
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    /**
     * Constructor with code and message
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * Constructor with code, message and cause
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
