package com.platform.report.domain.model.template;

import lombok.Getter;

/**
 * 模板布局
 * 定义报表的布局配置
 */
@Getter
public class TemplateLayout {
    
    private PaperSize paperSize;
    private Orientation orientation;
    private Margin margin;
    private int columns;
    private int rows;
    private GridType gridType;
    
    public TemplateLayout(PaperSize paperSize, Orientation orientation, 
                         Margin margin, int columns, int rows, 
                         GridType gridType) {
        this.paperSize = paperSize;
        this.orientation = orientation;
        this.margin = margin;
        this.columns = columns;
        this.rows = rows;
        this.gridType = gridType;
    }
    
    /**
     * 纸张大小枚举
     */
    public enum PaperSize {
        A4, A3, LETTER, LEGAL, CUSTOM
    }
    
    /**
     * 方向枚举
     */
    public enum Orientation {
        PORTRAIT, LANDSCAPE
    }
    
    /**
     * 网格类型枚举
     */
    public enum GridType {
        FIXED, FLUID, NONE
    }
    
    /**
     * 边距
     */
    @Getter
    public static class Margin {
        private final int top;
        private final int right;
        private final int bottom;
        private final int left;
        
        public Margin(int top, int right, int bottom, int left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }
    }
}
