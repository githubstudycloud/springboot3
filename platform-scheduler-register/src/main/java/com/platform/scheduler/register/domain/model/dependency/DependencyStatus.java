package com.platform.scheduler.register.domain.model.dependency;

/**
 * 依赖状态枚举
 * 表示依赖关系的生命周期状态
 * 
 * @author platform
 */
public enum DependencyStatus {
    
    /**
     * 活动状态，表示依赖关系当前生效
     */
    ACTIVE("活动"),
    
    /**
     * 禁用状态，表示依赖关系已被禁用，不再生效
     */
    DISABLED("禁用");
    
    private final String displayName;
    
    DependencyStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取状态的显示名称
     * 
     * @return 状态显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
}
