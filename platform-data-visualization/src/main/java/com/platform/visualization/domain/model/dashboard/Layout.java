package com.platform.visualization.domain.model.dashboard;

/**
 * 仪表板布局
 */
public class Layout {
    private LayoutType type;
    private int columns;
    private int rowHeight;
    private int margin;

    public Layout() {
        this.type = LayoutType.GRID;
        this.columns = 12;
        this.rowHeight = 30;
        this.margin = 10;
    }

    public LayoutType getType() {
        return type;
    }

    public void setType(LayoutType type) {
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

    /**
     * 布局类型
     */
    public enum LayoutType {
        GRID, FREE, FIXED
    }
}