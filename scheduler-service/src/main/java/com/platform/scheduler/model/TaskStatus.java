package com.platform.scheduler.model;

import java.util.Date;

import lombok.Data;

/**
 * 任务状态类，用于API响应
 * 
 * @author platform
 */
@Data
public class TaskStatus {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务状态(CREATED/ENABLED/DISABLED/PAUSED/RUNNING/FAILED)
     */
    private String status;
    
    /**
     * 上次执行时间
     */
    private Date lastExecutionTime;
    
    /**
     * 下次执行时间
     */
    private Date nextExecutionTime;
    
    /**
     * 当前执行ID
     */
    private Long currentExecutionId;
    
    /**
     * 当前进度
     */
    private Integer currentProgress;
    
    /**
     * 当前状态描述
     */
    private String currentStatusDesc;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 判断任务是否正在运行
     */
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }
    
    /**
     * 判断是否有当前执行
     */
    public boolean hasCurrentExecution() {
        return currentExecutionId != null;
    }
}
