package com.platform.domain.config.template.valueobject;

/**
 * 模板状态枚举
 * 定义配置模板的生命周期状态
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum TemplateStatus {
    
    /**
     * 草稿状态 - 可以修改
     */
    DRAFT("草稿", true, true),
    
    /**
     * 已发布状态 - 可以使用，不可修改
     */
    PUBLISHED("已发布", false, true),
    
    /**
     * 停用状态 - 不可使用，不可修改
     */
    INACTIVE("已停用", false, false),
    
    /**
     * 已删除状态 - 软删除
     */
    DELETED("已删除", false, false);
    
    private final String displayName;
    private final boolean canModify;
    private final boolean canUse;
    
    TemplateStatus(String displayName, boolean canModify, boolean canUse) {
        this.displayName = displayName;
        this.canModify = canModify;
        this.canUse = canUse;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean canModify() {
        return canModify;
    }
    
    public boolean canUse() {
        return canUse;
    }
    
    /**
     * 检查是否可以发布
     */
    public boolean canPublish() {
        return this == DRAFT;
    }
    
    /**
     * 检查是否可以停用
     */
    public boolean canDeactivate() {
        return this == PUBLISHED;
    }
    
    /**
     * 检查是否可以重新激活
     */
    public boolean canReactivate() {
        return this == INACTIVE;
    }
    
    /**
     * 检查是否可以删除
     */
    public boolean canDelete() {
        return this == DRAFT || this == INACTIVE;
    }
    
    /**
     * 检查是否可以创建新版本
     */
    public boolean canCreateNewVersion() {
        return this == PUBLISHED;
    }
} 