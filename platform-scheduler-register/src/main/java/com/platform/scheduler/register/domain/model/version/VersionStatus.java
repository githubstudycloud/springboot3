package com.platform.scheduler.register.domain.model.version;

/**
 * 版本状态枚举
 * 表示作业版本的状态
 * 
 * @author platform
 */
public enum VersionStatus {
    
    /**
     * 已创建状态，表示版本刚创建
     */
    CREATED("已创建"),
    
    /**
     * 已激活状态，表示版本当前正在使用
     */
    ACTIVATED("已激活"),
    
    /**
     * 已归档状态，表示版本已被归档，不再使用但保留记录
     */
    ARCHIVED("已归档");
    
    private final String displayName;
    
    VersionStatus(String displayName) {
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
    
    /**
     * 判断版本是否可恢复
     * 
     * @return 如果版本不是已激活状态，则返回true
     */
    public boolean isRestorable() {
        return this != ACTIVATED;
    }
}
