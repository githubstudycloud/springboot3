package com.platform.visualization.domain.model.chart;

import com.platform.visualization.domain.model.dataset.Field;

/**
 * 图表维度定义
 */
public class ChartDimension {
    private final String name;
    private final Field field;
    private final AggregationType aggregation;
    private final String alias;

    public ChartDimension(String name, Field field, AggregationType aggregation, String alias) {
        this.name = name;
        this.field = field;
        this.aggregation = aggregation;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public AggregationType getAggregation() {
        return aggregation;
    }

    public String getAlias() {
        return alias;
    }

    /**
     * 聚合类型
     */
    public enum AggregationType {
        NONE, SUM, AVG, MIN, MAX, COUNT, DISTINCT_COUNT, CUSTOM
    }
}