package com.platform.scheduler.model;

import java.util.Date;

import lombok.Data;

/**
 * 任务实体类
 * 
 * @author platform
 */
@Data
public class Task {
    
    /**
     * 任务ID
     */
    private Long id;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务类型(HTTP/SCRIPT/CLASS等)
     */
    private String type;
    
    /**
     * 任务目标(URL/脚本路径/类名)
     */
    private String target;
    
    /**
     * 任务参数(JSON格式)
     */
    private String parameters;
    
    /**
     * 调度类型(CRON/FIXED_RATE/FIXED_DELAY)
     */
    private String scheduleType;
    
    /**
     * Cron表达式
     */
    private String cronExpression;
    
    /**
     * 固定频率(毫秒)
     */
    private Long fixedRate;
    
    /**
     * 固定延迟(毫秒)
     */
    private Long fixedDelay;
    
    /**
     * 初始延迟(毫秒)
     */
    private Long initialDelay;
    
    /**
     * 超时时间(毫秒)
     */
    private Long timeout;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 重试间隔(毫秒)
     */
    private Long retryInterval;
    
    /**
     * 任务状态(CREATED/ENABLED/DISABLED/PAUSED/RUNNING/FAILED)
     */
    private String status;
    
    /**
     * 当前重试次数
     */
    private Integer currentRetryCount;
    
    /**
     * 上次执行时间
     */
    private Date lastExecutionTime;
    
    /**
     * 下次执行时间
     */
    private Date nextExecutionTime;
    
    /**
     * 创建时间
     */
    private Date createdTime;
    
    /**
     * 更新时间
     */
    private Date updatedTime;
    
    /**
     * 当前执行ID（非数据库字段，仅用于任务执行过程中）
     */
    private transient Long currentExecutionId;
    
    /**
     * 判断任务是否可运行
     */
    public boolean isExecutable() {
        return "ENABLED".equals(status) || "RETRY_PENDING".equals(status);
    }
    
    /**
     * 判断任务是否已启用
     */
    public boolean isEnabled() {
        return "ENABLED".equals(status);
    }
    
    /**
     * 判断任务是否正在运行
     */
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }
    
    /**
     * 判断任务是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    /**
     * 判断任务是否暂停
     */
    public boolean isPaused() {
        return "PAUSED".equals(status);
    }
    
    /**
     * 判断任务是否等待重试
     */
    public boolean isRetryPending() {
        return "RETRY_PENDING".equals(status);
    }
}
