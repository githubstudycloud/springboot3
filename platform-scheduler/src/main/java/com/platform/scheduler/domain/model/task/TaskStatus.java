package com.platform.scheduler.domain.model.task;

/**
 * 任务状态枚举
 * 定义任务实例的生命周期状态
 * 
 * @author platform
 */
public enum TaskStatus {
    
    /**
     * 等待中 - 任务已创建但尚未开始执行
     */
    WAITING("waiting", "等待中"),
    
    /**
     * 准备中 - 任务正在准备执行环境
     */
    PREPARING("preparing", "准备中"),
    
    /**
     * 运行中 - 任务正在执行
     */
    RUNNING("running", "运行中"),
    
    /**
     * 暂停中 - 任务被临时暂停
     */
    PAUSED("paused", "暂停中"),
    
    /**
     * 已取消 - 任务被手动取消
     */
    CANCELED("canceled", "已取消"),
    
    /**
     * 已完成 - 任务成功完成
     */
    COMPLETED("completed", "已完成"),
    
    /**
     * 已失败 - 任务执行失败
     */
    FAILED("failed", "已失败"),
    
    /**
     * 等待重试 - 任务执行失败，等待重试
     */
    WAITING_RETRY("waiting_retry", "等待重试"),
    
    /**
     * 已超时 - 任务执行超时
     */
    TIMEOUT("timeout", "已超时");
    
    private final String code;
    private final String description;
    
    TaskStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码查找任务状态
     *
     * @param code 状态代码
     * @return 对应的任务状态，如果未找到则返回null
     */
    public static TaskStatus fromCode(String code) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 判断任务是否处于终止状态
     *
     * @return 如果任务已完成、失败、取消或超时，则返回true
     */
    public boolean isTerminated() {
        return this == COMPLETED || this == FAILED || this == CANCELED || this == TIMEOUT;
    }
    
    /**
     * 判断任务是否处于活动状态
     *
     * @return 如果任务正在准备、运行或暂停，则返回true
     */
    public boolean isActive() {
        return this == PREPARING || this == RUNNING || this == PAUSED;
    }
    
    /**
     * 判断任务是否可重试
     *
     * @return 如果任务失败或超时，则可以重试，返回true
     */
    public boolean isRetryable() {
        return this == FAILED || this == TIMEOUT;
    }
}
