package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表板面板领域模型
 */
@Getter
public class DashboardPanel extends AbstractEntity<String> {
    
    /**
     * 面板ID
     */
    private final String id;
    
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
    private PanelType type;
    
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
    private final Map<String, Object> dataSourceConfig;
    
    /**
     * 展示配置
     */
    private final Map<String, Object> displayConfig;
    
    /**
     * 指标查询列表
     */
    private final List<MetricQuery> metricQueries;
    
    /**
     * 创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 刷新间隔（秒），如果为null则使用仪表板的刷新间隔
     */
    private Integer refreshIntervalSeconds;
    
    /**
     * 构造函数
     *
     * @param id 面板ID
     * @param title 面板标题
     * @param description 面板描述
     * @param type 面板类型
     * @param width 面板宽度
     * @param height 面板高度
     * @param positionX X坐标位置
     * @param positionY Y坐标位置
     */
    public DashboardPanel(String id, String title, String description, PanelType type,
                         int width, int height, int positionX, int positionY) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.width = width;
        this.height = height;
        this.positionX = positionX;
        this.positionY = positionY;
        this.dataSourceConfig = new HashMap<>();
        this.displayConfig = new HashMap<>();
        this.metricQueries = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新面板基本信息
     *
     * @param title 面板标题
     * @param description 面板描述
     * @param type 面板类型
     * @return 当前面板实例
     */
    public DashboardPanel updateBasicInfo(String title, String description, PanelType type) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 更新面板尺寸和位置
     *
     * @param width 面板宽度
     * @param height 面板高度
     * @param positionX X坐标位置
     * @param positionY Y坐标位置
     * @return 当前面板实例
     */
    public DashboardPanel updatePosition(int width, int height, int positionX, int positionY) {
        this.width = width;
        this.height = height;
        this.positionX = positionX;
        this.positionY = positionY;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 更新刷新间隔
     *
     * @param refreshIntervalSeconds 刷新间隔（秒）
     * @return 当前面板实例
     */
    public DashboardPanel updateRefreshInterval(Integer refreshIntervalSeconds) {
        this.refreshIntervalSeconds = refreshIntervalSeconds;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加数据源配置
     *
     * @param key 配置键
     * @param value 配置值
     * @return 当前面板实例
     */
    public DashboardPanel addDataSourceConfig(String key, Object value) {
        this.dataSourceConfig.put(key, value);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加展示配置
     *
     * @param key 配置键
     * @param value 配置值
     * @return 当前面板实例
     */
    public DashboardPanel addDisplayConfig(String key, Object value) {
        this.displayConfig.put(key, value);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加指标查询
     *
     * @param metricQuery 指标查询
     * @return 当前面板实例
     */
    public DashboardPanel addMetricQuery(MetricQuery metricQuery) {
        this.metricQueries.add(metricQuery);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 移除指标查询
     *
     * @param queryId 查询ID
     * @return 当前面板实例
     */
    public DashboardPanel removeMetricQuery(String queryId) {
        this.metricQueries.removeIf(query -> query.getId().equals(queryId));
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 获取有效的刷新间隔
     *
     * @param defaultRefreshInterval 默认刷新间隔
     * @return 有效的刷新间隔
     */
    public int getEffectiveRefreshInterval(int defaultRefreshInterval) {
        return refreshIntervalSeconds != null ? refreshIntervalSeconds : defaultRefreshInterval;
    }
}
