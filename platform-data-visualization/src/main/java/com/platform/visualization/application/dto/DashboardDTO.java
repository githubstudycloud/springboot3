package com.platform.visualization.application.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 仪表板数据传输对象
 */
public class DashboardDTO {
    private String id;
    private String name;
    private String description;
    private List<DashboardItemDTO> items;
    private LayoutDTO layout;
    private String theme;
    private boolean isPublic;

    public DashboardDTO() {
        this.items = new ArrayList<>();
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

    public List<DashboardItemDTO> getItems() {
        return items;
    }

    public LayoutDTO getLayout() {
        return layout;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isPublic() {
        return isPublic;
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

    public void setItems(List<DashboardItemDTO> items) {
        this.items = items;
    }

    public void setLayout(LayoutDTO layout) {
        this.layout = layout;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * 仪表板项DTO
     */
    public static class DashboardItemDTO {
        private String id;
        private String chartId;
        private PositionDTO position;

        // Getter和Setter方法
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getChartId() {
            return chartId;
        }

        public void setChartId(String chartId) {
            this.chartId = chartId;
        }

        public PositionDTO getPosition() {
            return position;
        }

        public void setPosition(PositionDTO position) {
            this.position = position;
        }
    }

    /**
     * 位置DTO
     */
    public static class PositionDTO {
        private int x;
        private int y;
        private int width;
        private int height;

        // Getter和Setter方法
        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    /**
     * 布局DTO
     */
    public static class LayoutDTO {
        private String type;
        private int columns;
        private int rowHeight;
        private int margin;

        // Getter和Setter方法
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getColumns() {
            return columns;
        }

        public void setColumns(int columns) {
            this.columns = columns;
        }

        public int getRowHeight() {
            return rowHeight;
        }

        public void setRowHeight(int rowHeight) {
            this.rowHeight = rowHeight;
        }

        public int getMargin() {
            return margin;
        }

        public void setMargin(int margin) {
            this.margin = margin;
        }
    }
}