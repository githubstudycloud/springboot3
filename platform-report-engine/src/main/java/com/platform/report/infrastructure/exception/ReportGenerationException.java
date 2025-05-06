package com.platform.report.infrastructure.exception;

/**
 * 报表生成异常
 */
public class ReportGenerationException extends RuntimeException {
    
    public ReportGenerationException(String message) {
        super(message);
    }
    
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
