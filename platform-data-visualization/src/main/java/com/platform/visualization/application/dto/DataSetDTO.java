package com.platform.visualization.application.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据集数据传输对象
 */
public class DataSetDTO {
    private String id;
    private String name;
    private String description;
    private String dataSourceId;
    private QueryDTO query;
    private List<FieldDTO> fields;
    private RefreshStrategyDTO refreshStrategy;
    private LocalDateTime lastRefreshedAt;
    private String status;

    public DataSetDTO() {
        this.fields = new ArrayList<>();
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

    public String getDataSourceId() {
        return dataSourceId;
    }

    public QueryDTO getQuery() {
        return query;
    }

    public List<FieldDTO> getFields() {
        return fields;
    }

    public RefreshStrategyDTO getRefreshStrategy() {
        return refreshStrategy;
    }

    public LocalDateTime getLastRefreshedAt() {
        return lastRefreshedAt;
    }

    public String getStatus() {
        return status;
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

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public void setQuery(QueryDTO query) {
        this.query = query;
    }

    public void setFields(List<FieldDTO> fields) {
        this.fields = fields;
    }

    public void setRefreshStrategy(RefreshStrategyDTO refreshStrategy) {
        this.refreshStrategy = refreshStrategy;
    }

    public void setLastRefreshedAt(LocalDateTime lastRefreshedAt) {
        this.lastRefreshedAt = lastRefreshedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 数据集查询DTO
     */
    public static class QueryDTO {
        private String queryText;
        private String queryType;
        private Integer timeout;

        // Getter和Setter方法
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
     * 数据集字段DTO
     */
    public static class FieldDTO {
        private String name;
        private String label;
        private String type;
        private String format;
        private boolean calculated;
        private String expression;

        // Getter和Setter方法
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
     * 刷新策略DTO
     */
    public static class RefreshStrategyDTO {
        private String type;
        private String cronExpression;
        private Long intervalSeconds;

        // Getter和Setter方法
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