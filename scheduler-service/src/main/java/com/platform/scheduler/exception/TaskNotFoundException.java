package com.platform.scheduler.exception;

/**
 * 任务不存在异常
 * 
 * @author platform
 */
public class TaskNotFoundException extends SchedulerException {

    private static final long serialVersionUID = 1L;
    
    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 构造函数
     * 
     * @param taskId 任务ID
     */
    public TaskNotFoundException(Long taskId) {
        super("TASK_NOT_FOUND", "任务不存在: " + taskId);
        this.taskId = taskId;
    }

    /**
     * 获取任务ID
     * 
     * @return 任务ID
     */
    public Long getTaskId() {
        return taskId;
    }
}
