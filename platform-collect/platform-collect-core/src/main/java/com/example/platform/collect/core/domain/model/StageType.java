package com.example.platform.collect.core.domain.model;

/**
 * 流水线阶段类型枚举
 * 定义采集流水线中各阶段的处理类型
 */
public enum StageType {
    /**
     * 数据提取
     * 从数据源获取原始数据的阶段
     */
    EXTRACT("数据提取", "从数据源获取原始数据"),
    
    /**
     * 数据过滤
     * 按条件筛选数据的阶段
     */
    FILTER("数据过滤", "按条件筛选数据"),
    
    /**
     * 数据转换
     * 进行数据格式或结构转换的阶段
     */
    TRANSFORM("数据转换", "进行数据格式或结构转换"),
    
    /**
     * 数据验证
     * 验证数据有效性和完整性的阶段
     */
    VALIDATE("数据验证", "验证数据有效性和完整性"),
    
    /**
     * 数据映射
     * 将源数据字段映射到目标字段的阶段
     */
    MAP("数据映射", "将源数据字段映射到目标字段"),
    
    /**
     * 数据丰富
     * 使用其他数据源补充数据的阶段
     */
    ENRICH("数据丰富", "使用其他数据源补充数据"),
    
    /**
     * 数据聚合
     * 对数据进行汇总或统计的阶段
     */
    AGGREGATE("数据聚合", "对数据进行汇总或统计"),
    
    /**
     * 数据分组
     * 按照特定规则将数据分组的阶段
     */
    GROUP("数据分组", "按照特定规则将数据分组"),
    
    /**
     * 数据分拆
     * 将数据拆分成多个部分的阶段
     */
    SPLIT("数据分拆", "将数据拆分成多个部分"),
    
    /**
     * 数据合并
     * 将多个数据源合并的阶段
     */
    MERGE("数据合并", "将多个数据源合并"),
    
    /**
     * 数据排序
     * 对数据进行排序的阶段
     */
    SORT("数据排序", "对数据进行排序"),
    
    /**
     * 数据去重
     * 移除重复数据的阶段
     */
    DEDUPLICATE("数据去重", "移除重复数据"),
    
    /**
     * 数据加载
     * 将处理后的数据加载到目标存储的阶段
     */
    LOAD("数据加载", "将处理后的数据加载到目标存储"),
    
    /**
     * 数据清洗
     * 清理不规范或错误数据的阶段
     */
    CLEANSE("数据清洗", "清理不规范或错误数据"),
    
    /**
     * 数据脱敏
     * 对敏感数据进行脱敏处理的阶段
     */
    MASK("数据脱敏", "对敏感数据进行脱敏处理"),
    
    /**
     * 数据审计
     * 记录数据处理过程和结果的阶段
     */
    AUDIT("数据审计", "记录数据处理过程和结果"),
    
    /**
     * 自定义处理
     * 实现特定业务逻辑的自定义处理阶段
     */
    CUSTOM("自定义处理", "实现特定业务逻辑的自定义处理");
    
    private final String displayName;
    private final String description;
    
    StageType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 获取阶段类型显示名称
     *
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取阶段类型描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
}
