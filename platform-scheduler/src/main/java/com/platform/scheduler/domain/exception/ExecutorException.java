package com.platform.scheduler.domain.exception;

/**
 * 执行器相关异常
 * 表示在执行器领域模型中发生的异常
 *
 * @author platform
 */
public class ExecutorException extends DomainException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 执行器ID
     */
    private final String executorId;
    
    /**
     * 构造方法
     *
     * @param executorId 执行器ID
     * @param message 异常消息
     */
    public ExecutorException(String executorId, String message) {
        super("EXECUTOR_ERROR", message);
        this.executorId = executorId;
    }
    
    /**
     * 构造方法
     *
     * @param executorId 执行器ID
     * @param errorCode 错误代码
     * @param message 异常消息
     */
    public ExecutorException(String executorId, String errorCode, String message) {
        super(errorCode, message);
        this.executorId = executorId;
    }
    
    /**
     * 构造方法
     *
     * @param executorId 执行器ID
     * @param errorCode 错误代码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public ExecutorException(String executorId, String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.executorId = executorId;
    }
    
    /**
     * 获取执行器ID
     *
     * @return 执行器ID
     */
    public String getExecutorId() {
        return executorId;
    }
    
    /**
     * 执行器未找到异常
     *
     * @param executorId 执行器ID
     * @return 执行器未找到异常
     */
    public static ExecutorException notFound(String executorId) {
        return new ExecutorException(executorId, "EXECUTOR_NOT_FOUND", "Executor not found: " + executorId);
    }
    
    /**
     * 执行器名称已存在异常
     *
     * @param name 执行器名称
     * @return 执行器名称已存在异常
     */
    public static ExecutorException nameAlreadyExists(String name) {
        return new ExecutorException(null, "EXECUTOR_NAME_ALREADY_EXISTS", "Executor name already exists: " + name);
    }
    
    /**
     * 执行器地址已存在异常
     *
     * @param host 主机地址
     * @param port 端口号
     * @return 执行器地址已存在异常
     */
    public static ExecutorException addressAlreadyExists(String host, int port) {
        return new ExecutorException(null, "EXECUTOR_ADDRESS_ALREADY_EXISTS", 
                "Executor address already exists: " + host + ":" + port);
    }
    
    /**
     * 执行器状态无效异常
     *
     * @param executorId 执行器ID
     * @param currentStatus 当前状态
     * @param expectedStatus 期望状态
     * @return 执行器状态无效异常
     */
    public static ExecutorException invalidStatus(String executorId, String currentStatus, String expectedStatus) {
        return new ExecutorException(executorId, "EXECUTOR_INVALID_STATUS", 
                "Invalid executor status: " + currentStatus + ", expected: " + expectedStatus);
    }
    
    /**
     * 执行器心跳超时异常
     *
     * @param executorId 执行器ID
     * @param lastHeartbeatTime 最后心跳时间
     * @return 执行器心跳超时异常
     */
    public static ExecutorException heartbeatTimeout(String executorId, String lastHeartbeatTime) {
        return new ExecutorException(executorId, "EXECUTOR_HEARTBEAT_TIMEOUT", 
                "Executor heartbeat timeout, last heartbeat time: " + lastHeartbeatTime);
    }
    
    /**
     * 执行器负载过高异常
     *
     * @param executorId 执行器ID
     * @param currentLoad 当前负载
     * @param maxConcurrency 最大并发数
     * @return 执行器负载过高异常
     */
    public static ExecutorException overloaded(String executorId, int currentLoad, int maxConcurrency) {
        return new ExecutorException(executorId, "EXECUTOR_OVERLOADED", 
                "Executor is overloaded, current load: " + currentLoad + ", max concurrency: " + maxConcurrency);
    }
}
