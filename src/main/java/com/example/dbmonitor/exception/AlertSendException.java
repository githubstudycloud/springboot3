package com.example.dbmonitor.exception;

public class AlertSendException extends Exception {
    private final String endpoint;
    
    public AlertSendException(String message, String endpoint) {
        super(message);
        this.endpoint = endpoint;
    }
    
    public AlertSendException(String message, String endpoint, Throwable cause) {
        super(message + " for endpoint: " + endpoint, cause);
        this.endpoint = endpoint;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
}