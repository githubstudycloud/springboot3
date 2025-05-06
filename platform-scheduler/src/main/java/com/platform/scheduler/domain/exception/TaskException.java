package com.platform.scheduler.domain.exception;

/**
 * 任务相关异常
 * 表示在任务领域模型中发生的异常
 *
 * @author platform
 */
public class TaskException extends DomainException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 任务实例ID
     */
    private final String taskInstanceId;
    
    /**
     * 构造方法
     *
     * @param taskInstanceId 任务实例ID
     * @param message 异常消息
     */
    public TaskException(String taskInstanceId, String message) {
        super("TASK_ERROR", message);
        this.taskInstanceId = taskInstanceId;
    }
    
    /**
     * 构造方法
     *
     * @param taskInstanceId 任务实例ID
     * @param errorCode 错误代码
     * @param message 异常消息
     */
    public TaskException(String taskInstanceId, String errorCode, String message) {
        super(errorCode, message);
        this.taskInstanceId = taskInstanceId;
    }
    
    /**
     * 构造方法
     *
     * @param taskInstanceId 任务实例ID
     * @param errorCode 错误代码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public TaskException(String taskInstanceId, String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.taskInstanceId = taskInstanceId;
    }
    
    /**
     * 获取任务实例ID
     *
     * @return 任务实例ID
     */
    public String getTaskInstanceId() {
        return taskInstanceId;
    }
    
    /**
     * 任务未找到异常
     *
     * @param taskInstanceId 任务实例ID
     * @return 任务未找到异常
     */
    public static TaskException notFound(String taskInstanceId) {
        return new TaskException(taskInstanceId, "TASK_NOT_FOUND", "Task not found: " + taskInstanceId);
    }
    
    /**
     * 任务状态无效异常
     *
     * @param taskInstanceId 任务实例ID
     * @param currentStatus 当前状态
     * @param expectedStatus 期望状态
     * @return 任务状态无效异常
     */
    public static TaskException invalidStatus(String taskInstanceId, String currentStatus, String expectedStatus) {
        return new TaskException(taskInstanceId, "TASK_INVALID_STATUS", 
                "Invalid task status: " + currentStatus + ", expected: " + expectedStatus);
    }
    
    /**
     * 任务已终止异常
     *
     * @param taskInstanceId 任务实例ID
     * @param status 当前状态
     * @return 任务已终止异常
     */
    public static TaskException alreadyTerminated(String taskInstanceId, String status) {
        return new TaskException(taskInstanceId, "TASK_ALREADY_TERMINATED", 
                "Task already terminated with status: " + status);
    }
    
    /**
     * 任务执行超时异常
     *
     * @param taskInstanceId 任务实例ID
     * @param timeoutSeconds 超时时间(秒)
     * @return 任务执行超时异常
     */
    public static TaskException executionTimeout(String taskInstanceId, int timeoutSeconds) {
        return new TaskException(taskInstanceId, "TASK_EXECUTION_TIMEOUT", 
                "Task execution timeout after " + timeoutSeconds + " seconds");
    }
    
    /**
     * 任务执行失败异常
     *
     * @param taskInstanceId 任务实例ID
     * @param errorMessage 错误信息
     * @return 任务执行失败异常
     */
    public static TaskException executionFailed(String taskInstanceId, String errorMessage) {
        return new TaskException(taskInstanceId, "TASK_EXECUTION_FAILED", 
                "Task execution failed: " + errorMessage);
    }
}
