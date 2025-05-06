package com.platform.visualization.domain.model.dataset;

/**
 * 数据集状态
 */
public enum DataSetStatus {
    CONFIGURING("配置中"),
    READY("就绪"),
    REFRESHING("刷新中"),
    ERROR("错误");

    private final String description;

    DataSetStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}