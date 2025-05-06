package com.platform.visualization.domain.model.datasource;

/**
 * 数据源类型
 */
public enum DataSourceType {
    MYSQL("MySQL数据库"),
    POSTGRESQL("PostgreSQL数据库"),
    ORACLE("Oracle数据库"),
    MONGODB("MongoDB数据库"),
    ELASTICSEARCH("Elasticsearch"),
    CSV("CSV文件"),
    EXCEL("Excel文件"),
    API("REST API"),
    CUSTOM("自定义数据源");

    private final String description;

    DataSourceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}