package com.platform.visualization.application.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 图表数据传输对象
 */
public class ChartDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private String dataSetId;
    private Map<String, String> options;
    private Map<String, ChartDimensionDTO> dimensions;

    public ChartDTO() {
        this.options = new HashMap<>();
        this.dimensions = new HashMap<>();
    }

    // Getter方法
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public Map<String, ChartDimensionDTO> getDimensions() {
        return dimensions;
    }

    // Setter方法
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public void setDimensions(Map<String, ChartDimensionDTO> dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * 图表维度DTO
     */
    public static class ChartDimensionDTO {
        private String name;
        private String fieldName;
        private String aggregation;
        private String alias;

        // Getter和Setter方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
}