package com.platform.domain.config.approval.valueobject;

/**
 * 优先级枚举
 * 定义配置变更申请的优先级
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum Priority {
    
    /**
     * 紧急 - 4小时内处理
     */
    URGENT("紧急", 1, 4, "系统故障、安全漏洞等紧急情况"),
    
    /**
     * 高 - 12小时内处理
     */
    HIGH("高", 2, 12, "影响业务功能的重要配置变更"),
    
    /**
     * 中 - 1天内处理
     */
    MEDIUM("中", 3, 24, "常规配置变更和优化"),
    
    /**
     * 低 - 3天内处理
     */
    LOW("低", 4, 72, "非关键配置调整和实验性变更");
    
    private final String displayName;
    private final int level;
    private final int maxHours;
    private final String description;
    
    Priority(String displayName, int level, int maxHours, String description) {
        this.displayName = displayName;
        this.level = level;
        this.maxHours = maxHours;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getMaxHours() {
        return maxHours;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为高优先级
     */
    public boolean isHighPriority() {
        return this == URGENT || this == HIGH;
    }
    
    /**
     * 检查是否需要立即处理
     */
    public boolean requiresImmediateAttention() {
        return this == URGENT;
    }
    
    /**
     * 获取建议的审批人数
     */
    public int getSuggestedApproverCount() {
        return switch (this) {
            case URGENT -> 1; // 紧急情况下单人审批
            case HIGH -> 2;   // 高优先级需要2人审批
            case MEDIUM -> 1; // 中等优先级单人审批
            case LOW -> 1;    // 低优先级单人审批
        };
    }
    
    /**
     * 比较优先级高低
     */
    public boolean isHigherThan(Priority other) {
        return this.level < other.level;
    }
    
    /**
     * 比较优先级高低
     */
    public boolean isLowerThan(Priority other) {
        return this.level > other.level;
    }
    
    /**
     * 根据配置类型推荐优先级
     */
    public static Priority recommendForConfigType(String configType, String environment) {
        if ("production".equalsIgnoreCase(environment)) {
            if (configType.contains("database") || configType.contains("security")) {
                return HIGH;
            }
            return MEDIUM;
        } else if ("staging".equalsIgnoreCase(environment)) {
            return MEDIUM;
        } else {
            return LOW;
        }
    }
} 