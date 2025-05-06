package com.example.platform.collect.core.domain.model;

/**
 * 任务状态枚举
 * 定义采集任务的各种状态
 */
public enum TaskStatus {
    /**
     * 已创建，初始状态
     */
    CREATED("已创建", "任务已创建但尚未调度执行"),
    
    /**
     * 已调度，等待执行
     */
    SCHEDULED("已调度", "任务已调度，等待执行"),
    
    /**
     * 正在执行
     */
    RUNNING("执行中", "任务正在执行"),
    
    /**
     * 已暂停
     */
    PAUSED("已暂停", "任务执行被暂停"),
    
    /**
     * 已完成
     */
    COMPLETED("已完成", "任务已成功完成"),
    
    /**
     * 失败
     */
    FAILED("失败", "任务执行失败"),
    
    /**
     * 已取消
     */
    CANCELLED("已取消", "任务被手动取消"),
    
    /**
     * 超时
     */
    TIMEOUT("超时", "任务执行超时"),
    
    /**
     * 已禁用
     */
    DISABLED("已禁用", "任务被禁用，不会被调度执行");
    
    private final String displayName;
    private final String description;
    
    TaskStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 获取任务状态显示名称
     *
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取任务状态描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断任务是否处于终态
     *
     * @return 是否是终态
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || 
               this == CANCELLED || this == TIMEOUT;
    }
    
    /**
     * 判断任务是否可以执行
     *
     * @return 是否可执行
     */
    public boolean isExecutable() {
        return this == CREATED || this == SCHEDULED || this == PAUSED;
    }
}
