package com.platform.visualization.domain.model.dashboard;

import com.platform.visualization.domain.model.chart.Chart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 仪表板领域模型
 * 代表一个包含多个图表的仪表板
 */
public class Dashboard {
    private DashboardId id;
    private String name;
    private String description;
    private List<DashboardItem> items;
    private Layout layout;
    private Theme theme;
    private boolean isPublic;

    public Dashboard() {
        this.id = new DashboardId();
        this.items = new ArrayList<>();
        this.layout = new Layout();
        this.theme = Theme.DEFAULT;
    }

    // 其他构造函数、Getter/Setter省略

    /**
     * 添加图表到仪表板
     * 
     * @param chart 要添加的图表
     * @param position 位置信息
     * @return 新添加的仪表板项
     */
    public DashboardItem addChart(Chart chart, Position position) {
        DashboardItem item = new DashboardItem(chart, position);
        items.add(item);
        return item;
    }

    /**
     * 移除仪表板项
     * 
     * @param itemId 要移除的仪表板项ID
     * @return 是否成功移除
     */
    public boolean removeItem(String itemId) {
        return items.removeIf(item -> item.getId().equals(itemId));
    }

    /**
     * 仪表板唯一标识
     */
    public static class DashboardId {
        private final String value;

        public DashboardId() {
            this.value = UUID.randomUUID().toString();
        }

        public DashboardId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}