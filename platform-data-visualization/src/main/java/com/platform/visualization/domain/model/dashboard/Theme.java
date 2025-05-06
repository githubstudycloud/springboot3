package com.platform.visualization.domain.model.dashboard;

/**
 * 仪表板主题
 */
public enum Theme {
    DEFAULT("默认"),
    DARK("暗色"),
    LIGHT("亮色"),
    BLUE("蓝色"),
    GREEN("绿色"),
    CUSTOM("自定义");

    private final String description;

    Theme(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}