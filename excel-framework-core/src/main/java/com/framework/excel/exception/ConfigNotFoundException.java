package com.framework.excel.exception;

/**
 * 配置未找到异常
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ConfigNotFoundException extends RuntimeException {
    
    public ConfigNotFoundException(String message) {
        super(message);
    }
    
    public ConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
