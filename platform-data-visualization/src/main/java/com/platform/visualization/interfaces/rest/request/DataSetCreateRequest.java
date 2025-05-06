package com.platform.visualization.interfaces.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据集创建请求
 */
public class DataSetCreateRequest {
    
    @NotBlank(message = "数据集名称不能为空")
    private String name;
    
    private String description;
    
    @NotBlank(message = "数据源ID不能为空")
    private String dataSourceId;
    
    @NotNull(message = "查询不能为空")
    @Valid
    private QueryRequest query;
    
    private List<FieldRequest> fields = new ArrayList<>();
    
    @Valid
    private RefreshStrategyRequest refreshStrategy;
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDataSourceId() {
        return dataSourceId;
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public QueryRequest getQuery() {
        return query;
    }
    
    public void setQuery(QueryRequest query) {
        this.query = query;
    }
    
    public List<FieldRequest> getFields() {
        return fields;
    }
    
    public void setFields(List<FieldRequest> fields) {
        this.fields = fields;
    }
    
    public RefreshStrategyRequest getRefreshStrategy() {
        return refreshStrategy;
    }
    
    public void setRefreshStrategy(RefreshStrategyRequest refreshStrategy) {
        this.refreshStrategy = refreshStrategy;
    }
    
    /**
     * 查询请求
     */
    public static class QueryRequest {
        
        @NotBlank(message = "查询文本不能为空")
        private String queryText;
        
        @NotBlank(message = "查询类型不能为空")
        private String queryType;
        
        private Integer timeout;
        
        // Getters and Setters
        public String getQueryText() {
            return queryText;
        }
        
        public void setQueryText(String queryText) {
            this.queryText = queryText;
        }
        
        public String getQueryType() {
            return queryType;
        }
        
        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }
        
        public Integer getTimeout() {
            return timeout;
        }
        
        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }
    }
    
    /**
     * 字段请求
     */
    public static class FieldRequest {
        
        @NotBlank(message = "字段名称不能为空")
        private String name;
        
        @NotBlank(message = "字段标签不能为空")
        private String label;
        
        @NotBlank(message = "字段类型不能为空")
        private String type;
        
        private String format;
        
        private boolean calculated;
        
        private String expression;
        
        // Getters and Setters
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
    
    /**
     * 刷新策略请求
     */
    public static class RefreshStrategyRequest {
        
        @NotBlank(message = "刷新类型不能为空")
        private String type;
        
        private String cronExpression;
        
        private Long intervalSeconds;
        
        // Getters and Setters
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getCronExpression() {
            return cronExpression;
        }
        
        public void setCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
        }
        
        public Long getIntervalSeconds() {
            return intervalSeconds;
        }
        
        public void setIntervalSeconds(Long intervalSeconds) {
            this.intervalSeconds = intervalSeconds;
        }
    }
}