package com.framework.excel.enums;

/**
 * 更新模式枚举
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public enum UpdateMode {
    INSERT_ONLY("INSERT_ONLY", "仅插入"),
    UPDATE_ONLY("UPDATE_ONLY", "仅更新"),
    INSERT_OR_UPDATE("INSERT_OR_UPDATE", "插入或更新");
    
    private final String code;
    private final String desc;
    
    UpdateMode(String code, String desc) {
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
    public static UpdateMode getByCode(String code) {
        for (UpdateMode mode : UpdateMode.values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        return INSERT_OR_UPDATE;
    }
}
