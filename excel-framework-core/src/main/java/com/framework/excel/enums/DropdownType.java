package com.framework.excel.enums;

/**
 * 下拉类型枚举
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public enum DropdownType {
    STATIC("STATIC", "静态选项"),
    RELATED_TABLE("RELATED_TABLE", "关联表");
    
    private final String code;
    private final String desc;
    
    DropdownType(String code, String desc) {
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
    public static DropdownType getByCode(String code) {
        for (DropdownType type : DropdownType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return STATIC;
    }
}
