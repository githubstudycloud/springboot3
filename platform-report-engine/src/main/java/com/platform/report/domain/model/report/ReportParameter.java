package com.platform.report.domain.model.report;

import lombok.Getter;

/**
 * 报表参数
 * 定义报表的动态参数
 */
@Getter
public class ReportParameter {
    
    private final String name;
    private final ParameterType type;
    private final boolean required;
    private final Object defaultValue;
    private final String label;
    private final String description;
    
    public ReportParameter(String name, ParameterType type, boolean required, 
                         Object defaultValue, String label, String description) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
        this.label = label;
        this.description = description;
    }
    
    /**
     * 参数类型枚举
     */
    public enum ParameterType {
        STRING,
        NUMBER,
        DATE,
        BOOLEAN,
        LIST,
        MULTI_SELECT
    }
}
