package com.framework.excel.enums;

/**
 * 数据类型枚举
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public enum DataType {
    STRING("STRING", "字符串"),
    INTEGER("INTEGER", "整数"),
    LONG("LONG", "长整数"),
    DOUBLE("DOUBLE", "小数"),
    DATE("DATE", "日期"),
    DATETIME("DATETIME", "日期时间"),
    BOOLEAN("BOOLEAN", "布尔值"),
    JSON("JSON", "JSON对象");
    
    private final String code;
    private final String desc;
    
    DataType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static DataType getByCode(String code) {
        for (DataType type : DataType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return STRING;
    }
}
