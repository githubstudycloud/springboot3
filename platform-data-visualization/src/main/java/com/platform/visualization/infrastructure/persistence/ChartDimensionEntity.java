package com.platform.visualization.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * 图表维度持久化实体
 */
@Entity
@Table(name = "viz_chart_dimension")
public class ChartDimensionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chart_id", nullable = false)
    private ChartEntity chart;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "dimension_key", nullable = false)
    private String dimensionKey;
    
    @Column(name = "field_name")
    private String fieldName;
    
    @Column
    private String aggregation;
    
    @Column
    private String alias;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChartEntity getChart() {
        return chart;
    }

    public void setChart(ChartEntity chart) {
        this.chart = chart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDimensionKey() {
        return dimensionKey;
    }

    public void setDimensionKey(String dimensionKey) {
        this.dimensionKey = dimensionKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}