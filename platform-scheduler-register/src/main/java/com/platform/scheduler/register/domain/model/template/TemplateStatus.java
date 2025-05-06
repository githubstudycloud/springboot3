package com.platform.scheduler.register.domain.model.template;

/**
 * 模板状态枚举
 * 表示模板的生命周期状态
 * 
 * @author platform
 */
public enum TemplateStatus {
    
    /**
     * 草稿状态，仅创建者可见
     */
    DRAFT("草稿"),
    
    /**
     * 已发布状态，所有用户可见并使用
     */
    PUBLISHED("已发布"),
    
    /**
     * 已禁用状态，不可用但仍可查看
     */
    DISABLED("已禁用");
    
    private final String displayName;
    
    TemplateStatus(String displayName) {
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
     * 判断模板是否可编辑
     * 
     * @return 如果状态为草稿则返回true
     */
    public boolean isEditable() {
        return this == DRAFT;
    }
    
    /**
     * 判断模板是否可用
     * 
     * @return 如果状态为已发布则返回true
     */
    public boolean isAvailable() {
        return this == PUBLISHED;
    }
}
