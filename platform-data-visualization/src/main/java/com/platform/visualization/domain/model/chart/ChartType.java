package com.platform.visualization.domain.model.chart;

/**
 * 图表类型
 */
public enum ChartType {
    BAR("柱状图"),
    LINE("折线图"),
    PIE("饼图"),
    SCATTER("散点图"),
    HEATMAP("热力图"),
    TABLE("表格"),
    GAUGE("仪表盘"),
    MAP("地图"),
    TREE("树图"),
    CUSTOM("自定义图表");

    private final String description;

    ChartType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}