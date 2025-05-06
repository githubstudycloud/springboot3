package com.platform.visualization.domain.model.dataset;

import com.platform.visualization.domain.model.datasource.DataSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 数据集领域模型
 * 代表从数据源中提取的数据集合
 */
public class DataSet {
    private DataSetId id;
    private String name;
    private String description;
    private DataSource dataSource;
    private Query query;
    private List<Field> fields;
    private RefreshStrategy refreshStrategy;
    private LocalDateTime lastRefreshedAt;
    private DataSetStatus status;

    // 构造函数、Getter/Setter省略

    /**
     * 刷新数据集
     */
    public void refresh() {
        // 领域逻辑 - 刷新数据
        this.lastRefreshedAt = LocalDateTime.now();
        this.status = DataSetStatus.READY;
    }

    /**
     * 验证数据集配置
     * 
     * @return 验证结果
     */
    public ValidationResult validate() {
        // 领域逻辑 - 验证配置
        return new ValidationResult(true, "验证成功");
    }

    /**
     * 数据集唯一标识
     */
    public static class DataSetId {
        private final String value;

        public DataSetId() {
            this.value = UUID.randomUUID().toString();
        }

        public DataSetId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}