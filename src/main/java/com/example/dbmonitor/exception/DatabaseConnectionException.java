package com.example.dbmonitor.exception;

public class DatabaseConnectionException extends DbMonitorException {
    public DatabaseConnectionException(String message, String dataSource) {
        super(message, dataSource);
    }
    
    public DatabaseConnectionException(String message, String dataSource, Throwable cause) {
        super(message, dataSource, cause);
    }
}