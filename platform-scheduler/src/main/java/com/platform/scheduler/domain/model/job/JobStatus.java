package com.platform.scheduler.domain.model.job;

/**
 * 作业状态枚举
 * 定义作业的生命周期状态
 * 
 * @author platform
 */
public enum JobStatus {
    
    /**
     * 已创建，尚未启用
     */
    CREATED("created", "已创建"),
    
    /**
     * 已启用，可被调度执行
     */
    ENABLED("enabled", "已启用"),
    
    /**
     * 已禁用，暂停调度
     */
    DISABLED("disabled", "已禁用"),
    
    /**
     * 已归档，不再使用但保留记录
     */
    ARCHIVED("archived", "已归档"),
    
    /**
     * 已删除，逻辑删除
     */
    DELETED("deleted", "已删除");
    
    private final String code;
    private final String description;
    
    JobStatus(String code, String description) {
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
     * 根据代码查找作业状态
     *
     * @param code 状态代码
     * @return 对应的作业状态，如果未找到则返回null
     */
    public static JobStatus fromCode(String code) {
        for (JobStatus status : JobStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 判断作业是否处于可执行状态
     *
     * @return 如果作业状态为已启用，则返回true
     */
    public boolean isExecutable() {
        return this == ENABLED;
    }
}
