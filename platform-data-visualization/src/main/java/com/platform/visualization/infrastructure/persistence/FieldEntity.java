package com.platform.visualization.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * 字段持久化实体
 */
@Entity
@Table(name = "viz_field")
public class FieldEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_set_id", nullable = false)
    private DataSetEntity dataSet;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String label;
    
    @Column(nullable = false)
    private String type;
    
    @Column
    private String format;
    
    @Column(name = "is_calculated", nullable = false)
    private boolean calculated;
    
    @Column(length = 1000)
    private String expression;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataSetEntity getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSetEntity dataSet) {
        this.dataSet = dataSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}