package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 仪表板面板DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPanelDTO {
    
    /**
     * 面板ID
     */
    private String id;
    
    /**
     * 面板标题
     */
    private String title;
    
    /**
     * 面板描述
     */
    private String description;
    
    /**
     * 面板类型
     */
    private String type;
    
    /**
     * 面板类型显示名称
     */
    private String typeDisplayName;
    
    /**
     * 面板宽度
     */
    private int width;
    
    /**
     * 面板高度
     */
    private int height;
    
    /**
     * X坐标位置
     */
    private int positionX;
    
    /**
     * Y坐标位置
     */
    private int positionY;
    
    /**
     * 数据源配置
     */
    private Map<String, Object> dataSourceConfig;
    
    /**
     * 展示配置
     */
    private Map<String, Object> displayConfig;
    
    /**
     * 指标查询列表
     */
    private List<MetricQueryDTO> metricQueries;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 刷新间隔（秒）
     */
    private Integer refreshIntervalSeconds;
    
    /**
     * 面板数据
     */
    private Map<String, Object> panelData;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 是否需要刷新数据
     */
    private boolean needRefresh;
    
    /**
     * 上次数据更新时间
     */
    private LocalDateTime lastDataUpdateTime;
}
