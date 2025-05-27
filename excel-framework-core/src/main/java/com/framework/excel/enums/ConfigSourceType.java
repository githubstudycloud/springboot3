package com.framework.excel.enums;

/**
 * 配置源类型枚举
 * 
 * @author framework
 * @since 1.0.0
 */
public enum ConfigSourceType {
    
    /**
     * YAML文件配置
     */
    YAML_FILE("yaml_file", "YAML文件配置"),
    
    /**
     * 数据库配置
     */
    DATABASE("database", "数据库配置"),
    
    /**
     * 混合配置(优先数据库，降级到文件)
     */
    HYBRID("hybrid", "混合配置");
    
    private final String code;
    private final String description;
    
    ConfigSourceType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ConfigSourceType fromCode(String code) {
        for (ConfigSourceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return HYBRID; // 默认混合模式
    }
} 