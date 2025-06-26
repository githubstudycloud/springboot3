package com.example.dbmonitor.exception;

// 基础异常类
public class DbMonitorException extends Exception {
    private final String dataSource;
    
    public DbMonitorException(String message, String dataSource) {
        super(message);
        this.dataSource = dataSource;
    }
    
    public DbMonitorException(String message, String dataSource, Throwable cause) {
        super(message, cause);
        this.dataSource = dataSource;
    }
    
    public String getDataSource() {
        return dataSource;
    }
}