package com.platform.scheduler.exception;

/**
 * 任务执行异常
 * 
 * @author platform
 */
public class TaskExecutionException extends SchedulerException {

    private static final long serialVersionUID = 1L;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 执行ID
     */
    private Long executionId;

    /**
     * 构造函数
     * 
     * @param taskId 任务ID
     * @param message 错误消息
     */
    public TaskExecutionException(Long taskId, String message) {
        super("TASK_EXECUTION_ERROR", message);
        this.taskId = taskId;
    }

    /**
     * 构造函数
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param message 错误消息
     */
    public TaskExecutionException(Long taskId, Long executionId, String message) {
        super("TASK_EXECUTION_ERROR", message);
        this.taskId = taskId;
        this.executionId = executionId;
    }
    
    /**
     * 构造函数
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param message 错误消息
     * @param cause 异常原因
     */
    public TaskExecutionException(Long taskId, Long executionId, String message, Throwable cause) {
        super("TASK_EXECUTION_ERROR", message, cause);
        this.taskId = taskId;
        this.executionId = executionId;
    }

    /**
     * 获取任务ID
     * 
     * @return 任务ID
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * 获取执行ID
     * 
     * @return 执行ID
     */
    public Long getExecutionId() {
        return executionId;
    }
}
