package com.platform.report.infrastructure.exception;

/**
 * 分发异常
 */
public class DistributionException extends RuntimeException {
    
    public DistributionException(String message) {
        super(message);
    }
    
    public DistributionException(String message, Throwable cause) {
        super(message, cause);
    }
}
