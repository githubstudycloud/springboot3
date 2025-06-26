package com.example.dbmonitor.exception;

public class SqlExecutionException extends DbMonitorException {
    private final String sql;
    
    public SqlExecutionException(String message, String dataSource, String sql) {
        super(message, dataSource);
        this.sql = sql;
    }
    
    public SqlExecutionException(String message, String dataSource, String sql, Throwable cause) {
        super(message, dataSource, cause);
        this.sql = sql;
    }
    
    public String getSql() {
        return sql;
    }
}