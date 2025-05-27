package com.framework.excel.enums;

/**
 * 数据更新模式枚举
 *
 * @author framework
 * @since 1.0.0
 */
public enum UpdateMode {

    /**
     * 仅插入模式
     */
    INSERT_ONLY("INSERT_ONLY", "仅插入"),

    /**
     * 仅更新模式
     */
    UPDATE_ONLY("UPDATE_ONLY", "仅更新"),

    /**
     * 插入或更新模式
     */
    INSERT_OR_UPDATE("INSERT_OR_UPDATE", "插入或更新");

    private final String code;
    private final String description;

    UpdateMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UpdateMode fromCode(String code) {
        for (UpdateMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        return INSERT_OR_UPDATE; // 默认插入或更新
    }
}