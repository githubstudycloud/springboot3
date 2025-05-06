package com.platform.visualization.domain.model.datasource;

import java.util.Map;
import java.util.UUID;

/**
 * 数据源领域模型
 * 代表一个可以提供数据的来源
 */
public class DataSource {
    private DataSourceId id;
    private String name;
    private String description;
    private DataSourceType type;
    private ConnectionProperties connectionProperties;
    private boolean active;
    private Map<String, String> metadata;

    // 构造函数、Getter/Setter省略

    /**
     * 测试数据源连接
     * 
     * @return 连接测试结果
     */
    public ConnectionTestResult testConnection() {
        // 领域逻辑
        return new ConnectionTestResult(true, "连接成功");
    }

    /**
     * 生成数据源的唯一标识符
     */
    public static class DataSourceId {
        private final String value;

        public DataSourceId() {
            this.value = UUID.randomUUID().toString();
        }

        public DataSourceId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}