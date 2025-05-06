package com.platform.report.infrastructure.exception;

/**
 * 计划执行异常
 */
public class ScheduleExecutionException extends RuntimeException {
    
    public ScheduleExecutionException(String message) {
        super(message);
    }
    
    public ScheduleExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
