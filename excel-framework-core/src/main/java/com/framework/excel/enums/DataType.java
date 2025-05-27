package com.framework.excel.enums;

/**
 * Excel字段数据类型枚举
 * 
 * @author framework
 * @since 1.0.0
 */
public enum DataType {
    
    STRING("STRING", "字符串"),
    INTEGER("INTEGER", "整数"),
    LONG("LONG", "长整数"),
    DOUBLE("DOUBLE", "小数"),
    DATE("DATE", "日期"),
    DATETIME("DATETIME", "日期时间"),
    BOOLEAN("BOOLEAN", "布尔值");
    
    private final String code;
    private final String description;
    
    DataType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static DataType fromCode(String code) {
        for (DataType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return STRING; // 默认字符串类型
    }
}