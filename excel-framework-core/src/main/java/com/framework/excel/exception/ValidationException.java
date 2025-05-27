package com.framework.excel.exception;

/**
 * 验证异常
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
