package com.platform.domain.config.approval.valueobject;

/**
 * 审批类型枚举
 * 定义不同类型的配置变更审批
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum ApprovalType {
    
    /**
     * 配置新增
     */
    CONFIG_CREATE("配置新增", "新增配置项", true),
    
    /**
     * 配置修改
     */
    CONFIG_UPDATE("配置修改", "修改现有配置", true),
    
    /**
     * 配置删除
     */
    CONFIG_DELETE("配置删除", "删除配置项", true),
    
    /**
     * 配置发布
     */
    CONFIG_PUBLISH("配置发布", "发布配置到环境", true),
    
    /**
     * 配置回滚
     */
    CONFIG_ROLLBACK("配置回滚", "回滚到历史版本", false),
    
    /**
     * 批量配置变更
     */
    CONFIG_BATCH_UPDATE("批量配置变更", "批量修改多个配置", true),
    
    /**
     * 环境配置迁移
     */
    CONFIG_MIGRATION("环境配置迁移", "配置在环境间迁移", true),
    
    /**
     * 敏感配置变更
     */
    SENSITIVE_CONFIG_UPDATE("敏感配置变更", "修改敏感配置信息", true),
    
    /**
     * 生产环境配置变更
     */
    PRODUCTION_CONFIG_UPDATE("生产环境配置变更", "生产环境配置修改", true),
    
    /**
     * 紧急配置变更
     */
    EMERGENCY_CONFIG_UPDATE("紧急配置变更", "紧急情况下的配置修改", false);
    
    private final String displayName;
    private final String description;
    private final boolean requiresApproval;
    
    ApprovalType(String displayName, String description, boolean requiresApproval) {
        this.displayName = displayName;
        this.description = description;
        this.requiresApproval = requiresApproval;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean requiresApproval() {
        return requiresApproval;
    }
    
    /**
     * 检查是否为敏感操作
     */
    public boolean isSensitive() {
        return this == SENSITIVE_CONFIG_UPDATE || 
               this == PRODUCTION_CONFIG_UPDATE || 
               this == CONFIG_DELETE;
    }
    
    /**
     * 检查是否为紧急操作
     */
    public boolean isEmergency() {
        return this == EMERGENCY_CONFIG_UPDATE || 
               this == CONFIG_ROLLBACK;
    }
    
    /**
     * 检查是否为批量操作
     */
    public boolean isBatchOperation() {
        return this == CONFIG_BATCH_UPDATE || 
               this == CONFIG_MIGRATION;
    }
    
    /**
     * 获取建议的审批级别
     */
    public int getSuggestedApprovalLevel() {
        return switch (this) {
            case CONFIG_CREATE, CONFIG_UPDATE -> 1;
            case CONFIG_PUBLISH, CONFIG_BATCH_UPDATE -> 2;
            case SENSITIVE_CONFIG_UPDATE, PRODUCTION_CONFIG_UPDATE -> 3;
            case CONFIG_DELETE, CONFIG_MIGRATION -> 2;
            case CONFIG_ROLLBACK, EMERGENCY_CONFIG_UPDATE -> 1;
        };
    }
} 