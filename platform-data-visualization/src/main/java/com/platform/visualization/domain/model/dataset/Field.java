package com.platform.visualization.domain.model.dataset;

/**
 * 数据集字段定义
 */
public class Field {
    private final String name;
    private final String label;
    private final FieldType type;
    private final String format;
    private final boolean isCalculated;
    private final String expression;

    public Field(String name, String label, FieldType type, String format, 
                boolean isCalculated, String expression) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.format = format;
        this.isCalculated = isCalculated;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public FieldType getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    public boolean isCalculated() {
        return isCalculated;
    }

    public String getExpression() {
        return expression;
    }

    /**
     * 字段类型
     */
    public enum FieldType {
        STRING, NUMBER, DATE, BOOLEAN, OBJECT, ARRAY
    }
}