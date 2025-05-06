package com.platform.visualization.interfaces.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 仪表板创建请求
 */
public class DashboardCreateRequest {
    
    @NotBlank(message = "仪表板名称不能为空")
    private String name;
    
    private String description;
    
    private List<@Valid DashboardItemRequest> items = new ArrayList<>();
    
    @Valid
    private LayoutRequest layout;
    
    private String theme;
    
    private boolean isPublic;
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<DashboardItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<DashboardItemRequest> items) {
        this.items = items;
    }
    
    public LayoutRequest getLayout() {
        return layout;
    }
    
    public void setLayout(LayoutRequest layout) {
        this.layout = layout;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    /**
     * 仪表板项请求
     */
    public static class DashboardItemRequest {
        
        private String id;
        
        @NotBlank(message = "图表ID不能为空")
        private String chartId;
        
        @NotNull(message = "位置信息不能为空")
        @Valid
        private PositionRequest position;
        
        // Getters and Setters
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
        
        public PositionRequest getPosition() {
            return position;
        }
        
        public void setPosition(PositionRequest position) {
            this.position = position;
        }
    }
    
    /**
     * 位置请求
     */
    public static class PositionRequest {
        
        private int x;
        
        private int y;
        
        private int width;
        
        private int height;
        
        // Getters and Setters
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
     * 布局请求
     */
    public static class LayoutRequest {
        
        @NotBlank(message = "布局类型不能为空")
        private String type;
        
        private int columns = 12;
        
        private int rowHeight = 30;
        
        private int margin = 10;
        
        // Getters and Setters
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