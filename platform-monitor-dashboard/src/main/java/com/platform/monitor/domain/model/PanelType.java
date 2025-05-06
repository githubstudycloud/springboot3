package com.platform.monitor.domain.model;

/**
 * 面板类型枚举
 */
public enum PanelType {
    
    /**
     * 折线图
     */
    LINE_CHART("LINE_CHART", "折线图"),
    
    /**
     * 柱状图
     */
    BAR_CHART("BAR_CHART", "柱状图"),
    
    /**
     * 饼图
     */
    PIE_CHART("PIE_CHART", "饼图"),
    
    /**
     * 仪表盘
     */
    GAUGE("GAUGE", "仪表盘"),
    
    /**
     * 状态卡片
     */
    STATUS_CARD("STATUS_CARD", "状态卡片"),
    
    /**
     * 数值卡片
     */
    METRIC_CARD("METRIC_CARD", "数值卡片"),
    
    /**
     * 表格
     */
    TABLE("TABLE", "表格"),
    
    /**
     * 热力图
     */
    HEATMAP("HEATMAP", "热力图"),
    
    /**
     * 时钟
     */
    CLOCK("CLOCK", "时钟"),
    
    /**
     * 文本
     */
    TEXT("TEXT", "文本"),
    
    /**
     * 告警列表
     */
    ALERT_LIST("ALERT_LIST", "告警列表"),
    
    /**
     * 服务地图
     */
    SERVICE_MAP("SERVICE_MAP", "服务地图"),
    
    /**
     * 散点图
     */
    SCATTER_PLOT("SCATTER_PLOT", "散点图"),
    
    /**
     * 面积图
     */
    AREA_CHART("AREA_CHART", "面积图"),
    
    /**
     * 气泡图
     */
    BUBBLE_CHART("BUBBLE_CHART", "气泡图"),
    
    /**
     * 雷达图
     */
    RADAR_CHART("RADAR_CHART", "雷达图"),
    
    /**
     * 自定义HTML
     */
    CUSTOM_HTML("CUSTOM_HTML", "自定义HTML"),
    
    /**
     * 嵌入式iframe
     */
    IFRAME("IFRAME", "嵌入式iframe");
    
    private final String code;
    private final String displayName;
    
    PanelType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * 获取面板类型代码
     * 
     * @return 面板类型代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取面板类型显示名称
     * 
     * @return 面板类型显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据代码获取面板类型
     * 
     * @param code 面板类型代码
     * @return 面板类型枚举值，如果找不到则返回LINE_CHART
     */
    public static PanelType fromCode(String code) {
        for (PanelType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return LINE_CHART;
    }
}
