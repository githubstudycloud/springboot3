package com.platform.visualization.domain.model.dashboard;

import com.platform.visualization.domain.model.chart.Chart;

import java.util.UUID;

/**
 * 仪表板项
 * 代表仪表板中的一个可视化组件
 */
public class DashboardItem {
    private final String id;
    private final Chart chart;
    private Position position;

    public DashboardItem(Chart chart, Position position) {
        this.id = UUID.randomUUID().toString();
        this.chart = chart;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public Chart getChart() {
        return chart;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}