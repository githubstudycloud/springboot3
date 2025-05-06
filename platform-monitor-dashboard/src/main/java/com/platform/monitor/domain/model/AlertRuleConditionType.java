package com.platform.monitor.domain.model;

/**
 * 告警规则条件类型枚举
 */
public enum AlertRuleConditionType {
    
    /**
     * 大于 - 指标值大于阈值时触发
     */
    GREATER_THAN("GREATER_THAN", "大于", ">"),
    
    /**
     * 大于等于 - 指标值大于等于阈值时触发
     */
    GREATER_THAN_OR_EQUAL("GREATER_THAN_OR_EQUAL", "大于等于", ">="),
    
    /**
     * 小于 - 指标值小于阈值时触发
     */
    LESS_THAN("LESS_THAN", "小于", "<"),
    
    /**
     * 小于等于 - 指标值小于等于阈值时触发
     */
    LESS_THAN_OR_EQUAL("LESS_THAN_OR_EQUAL", "小于等于", "<="),
    
    /**
     * 等于 - 指标值等于阈值时触发
     */
    EQUAL("EQUAL", "等于", "=="),
    
    /**
     * 不等于 - 指标值不等于阈值时触发
     */
    NOT_EQUAL("NOT_EQUAL", "不等于", "!="),
    
    /**
     * 范围内 - 指标值在范围内时触发
     */
    BETWEEN("BETWEEN", "范围内", "between"),
    
    /**
     * 范围外 - 指标值在范围外时触发
     */
    NOT_BETWEEN("NOT_BETWEEN", "范围外", "not between"),
    
    /**
     * 变化率超过 - 指标值变化率超过阈值时触发
     */
    RATE_OF_CHANGE("RATE_OF_CHANGE", "变化率超过", "rate >"),
    
    /**
     * 状态变更 - 当指标状态发生变化时触发
     */
    STATE_CHANGED("STATE_CHANGED", "状态变更", "state changed"),
    
    /**
     * 包含 - 当指标值包含特定字符串时触发
     */
    CONTAINS("CONTAINS", "包含", "contains"),
    
    /**
     * 不包含 - 当指标值不包含特定字符串时触发
     */
    NOT_CONTAINS("NOT_CONTAINS", "不包含", "not contains"),
    
    /**
     * 正则匹配 - 当指标值满足正则表达式时触发
     */
    REGEX_MATCH("REGEX_MATCH", "正则匹配", "regex");
    
    private final String code;
    private final String displayName;
    private final String operator;
    
    AlertRuleConditionType(String code, String displayName, String operator) {
        this.code = code;
        this.displayName = displayName;
        this.operator = operator;
    }
    
    /**
     * 获取条件类型代码
     * 
     * @return 条件类型代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取条件类型显示名称
     * 
     * @return 条件类型显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取条件类型运算符
     * 
     * @return 条件类型运算符
     */
    public String getOperator() {
        return operator;
    }
    
    /**
     * 根据代码获取条件类型
     * 
     * @param code 条件类型代码
     * @return 条件类型枚举值，如果找不到则返回GREATER_THAN
     */
    public static AlertRuleConditionType fromCode(String code) {
        for (AlertRuleConditionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return GREATER_THAN;
    }
}
