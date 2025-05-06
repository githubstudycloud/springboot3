package com.example.platform.governance.core.domain.model.rule;

/**
 * 规则状态枚举
 * 
 * 定义规则的生命周期状态
 */
public enum RuleStatus {
    
    /**
     * 草稿状态 - 初始创建未完成
     */
    DRAFT("草稿"),
    
    /**
     * 待审核状态 - 等待审核
     */
    PENDING_APPROVAL("待审核"),
    
    /**
     * 已发布状态 - 正式可用
     */
    PUBLISHED("已发布"),
    
    /**
     * 已弃用状态 - 不再推荐使用但仍可访问
     */
    DEPRECATED("已弃用"),
    
    /**
     * 已归档状态 - 不再可用
     */
    ARCHIVED("已归档");
    
    private final String displayName;
    
    RuleStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取状态显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 检查当前状态是否可以转换到目标状态
     * 
     * @param targetStatus 目标状态
     * @return 是否可以转换
     */
    public boolean canTransitionTo(RuleStatus targetStatus) {
        if (this == targetStatus) {
            return true; // 相同状态始终可以转换
        }
        
        switch (this) {
            case DRAFT:
                // 草稿可以转换为待审核或直接发布
                return targetStatus == PENDING_APPROVAL || targetStatus == PUBLISHED;
                
            case PENDING_APPROVAL:
                // 待审核可以转换为已发布（审核通过）或回到草稿（审核不通过）
                return targetStatus == PUBLISHED || targetStatus == DRAFT;
                
            case PUBLISHED:
                // 已发布可以转换为已弃用或已归档
                return targetStatus == DEPRECATED || targetStatus == ARCHIVED;
                
            case DEPRECATED:
                // 已弃用只能转换为已归档
                return targetStatus == ARCHIVED;
                
            case ARCHIVED:
                // 已归档是终态，不能转换到其他状态
                return false;
                
            default:
                return false;
        }
    }
    
    /**
     * 检查当前状态是否为活跃状态（可用状态）
     * 
     * @return 是否为活跃状态
     */
    public boolean isActive() {
        return this == PUBLISHED || this == DEPRECATED;
    }
}
