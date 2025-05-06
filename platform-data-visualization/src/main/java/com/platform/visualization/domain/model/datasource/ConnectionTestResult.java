package com.platform.visualization.domain.model.datasource;

/**
 * 数据源连接测试结果
 */
public class ConnectionTestResult {
    private final boolean success;
    private final String message;

    public ConnectionTestResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}