package com.example.platform.collect.core.domain.model;

/**
 * 采集模式枚举
 * 定义数据采集任务的执行模式
 */
public enum CollectMode {
    /**
     * 全量采集
     * 每次执行时采集所有数据，不考虑历史采集状态
     */
    FULL("全量采集", "采集数据源中的所有数据，不考虑历史采集状态"),
    
    /**
     * 增量采集
     * 只采集上次采集后新增或变更的数据，需要水印支持
     */
    INCREMENTAL("增量采集", "只采集上次采集后新增或变更的数据，需要水印支持"),
    
    /**
     * 差异采集
     * 计算当前数据与目标系统中数据的差异，只采集有变化的部分
     */
    DIFFERENTIAL("差异采集", "计算数据源与目标系统的差异，只采集有变化的部分"),
    
    /**
     * 自定义采集
     * 根据特定条件或规则进行采集，用于复杂场景
     */
    CUSTOM("自定义采集", "根据特定条件或规则进行采集，用于复杂场景");
    
    private final String displayName;
    private final String description;
    
    CollectMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 获取采集模式显示名称
     *
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取采集模式描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
}
