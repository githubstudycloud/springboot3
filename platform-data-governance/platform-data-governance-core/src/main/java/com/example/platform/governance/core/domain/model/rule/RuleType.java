package com.example.platform.governance.core.domain.model.rule;

/**
 * 规则类型枚举
 * 
 * 定义系统支持的规则类型
 */
public enum RuleType {
    
    /**
     * 数据质量规则
     */
    QUALITY("质量规则"),
    
    /**
     * 数据清洗规则
     */
    CLEANSING("清洗规则"),
    
    /**
     * 数据转换规则
     */
    TRANSFORMATION("转换规则"),
    
    /**
     * 数据格式化规则
     */
    FORMATTING("格式化规则"),
    
    /**
     * 数据验证规则
     */
    VALIDATION("验证规则"),
    
    /**
     * 数据脱敏规则
     */
    MASKING("脱敏规则"),
    
    /**
     * 业务规则
     */
    BUSINESS("业务规则"),
    
    /**
     * 其他规则
     */
    OTHER("其他规则");
    
    private final String displayName;
    
    RuleType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取规则类型的显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据显示名称获取规则类型枚举
     * 
     * @param displayName 显示名称
     * @return 对应的规则类型枚举，如果不存在则返回OTHER
     */
    public static RuleType fromDisplayName(String displayName) {
        for (RuleType type : values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return OTHER;
    }
}
