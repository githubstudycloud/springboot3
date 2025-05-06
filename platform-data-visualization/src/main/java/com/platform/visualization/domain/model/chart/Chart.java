package com.platform.visualization.domain.model.chart;

import com.platform.visualization.domain.model.dataset.DataSet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图表领域模型
 * 代表一个可视化图表
 */
public class Chart {
    private ChartId id;
    private String name;
    private String description;
    private ChartType type;
    private DataSet dataSet;
    private Map<String, String> options;
    private Map<String, ChartDimension> dimensions;

    // 构造函数、Getter/Setter省略

    /**
     * 更新图表配置
     * 
     * @param options 新配置选项
     */
    public void updateOptions(Map<String, String> options) {
        // 领域逻辑
        this.options = new HashMap<>(options);
    }

    /**
     * 验证图表配置
     * 
     * @return 验证结果
     */
    public boolean validate() {
        // 领域逻辑 - 验证图表配置
        return true;
    }

    /**
     * 图表唯一标识
     */
    public static class ChartId {
        private final String value;

        public ChartId() {
            this.value = UUID.randomUUID().toString();
        }

        public ChartId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}