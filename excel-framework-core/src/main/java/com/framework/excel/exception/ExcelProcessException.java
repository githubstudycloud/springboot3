package com.framework.excel.exception;

/**
 * Excel处理异常
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ExcelProcessException extends RuntimeException {
    
    public ExcelProcessException(String message) {
        super(message);
    }
    
    public ExcelProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
