package com.example.platform.collect.core.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 水印配置模型
 * 定义增量采集时的水印跟踪策略
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WatermarkConfig {
    
    /**
     * 水印字段配置列表
     * 支持多字段组合作为水印
     */
    private List<WatermarkField> watermarkFields;
    
    /**
     * 水印存储键
     * 用于唯一标识当前任务的水印记录
     */
    private String watermarkKey;
    
    /**
     * 初始水印值
     * 首次执行时使用的水印值
     */
    private Map<String, Object> initialWatermarks;
    
    /**
     * 水印更新策略
     */
    private WatermarkUpdateStrategy updateStrategy;
    
    /**
     * 构造函数
     */
    @Builder
    public WatermarkConfig(List<WatermarkField> watermarkFields, String watermarkKey, 
                         Map<String, Object> initialWatermarks, 
                         WatermarkUpdateStrategy updateStrategy) {
        this.watermarkFields = watermarkFields != null ? watermarkFields : new ArrayList<>();
        this.watermarkKey = watermarkKey;
        this.initialWatermarks = initialWatermarks != null ? initialWatermarks : new HashMap<>();
        this.updateStrategy = updateStrategy != null ? 
                             updateStrategy : WatermarkUpdateStrategy.AFTER_BATCH;
    }
    
    /**
     * 添加水印字段
     *
     * @param field 水印字段
     */
    public void addWatermarkField(WatermarkField field) {
        if (this.watermarkFields == null) {
            this.watermarkFields = new ArrayList<>();
        }
        this.watermarkFields.add(field);
    }
    
    /**
     * 设置初始水印值
     *
     * @param fieldName 字段名
     * @param value 初始值
     */
    public void setInitialWatermark(String fieldName, Object value) {
        if (this.initialWatermarks == null) {
            this.initialWatermarks = new HashMap<>();
        }
        this.initialWatermarks.put(fieldName, value);
    }
    
    /**
     * 判断水印配置是否有效
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return watermarkFields != null && !watermarkFields.isEmpty() && 
               watermarkKey != null && !watermarkKey.trim().isEmpty();
    }
    
    /**
     * 水印字段模型
     * 定义单个水印字段的属性
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WatermarkField {
        
        /**
         * 字段名
         */
        private String fieldName;
        
        /**
         * 字段类型
         */
        private WatermarkFieldType fieldType;
        
        /**
         * 字段路径
         * 用于从复杂对象中提取字段值
         */
        private String fieldPath;
        
        /**
         * 比较策略
         */
        private ComparisonStrategy comparisonStrategy;
        
        /**
         * 构造函数
         */
        @Builder
        public WatermarkField(String fieldName, WatermarkFieldType fieldType, 
                             String fieldPath, ComparisonStrategy comparisonStrategy) {
            this.fieldName = fieldName;
            this.fieldType = fieldType != null ? fieldType : WatermarkFieldType.TIMESTAMP;
            this.fieldPath = fieldPath != null ? fieldPath : fieldName;
            this.comparisonStrategy = comparisonStrategy != null ? 
                                     comparisonStrategy : ComparisonStrategy.GREATER_THAN;
        }
    }
    
    /**
     * 水印字段类型枚举
     */
    public enum WatermarkFieldType {
        /**
         * 时间戳类型
         */
        TIMESTAMP,
        
        /**
         * 整数类型
         */
        INTEGER,
        
        /**
         * 字符串类型
         */
        STRING,
        
        /**
         * 版本号类型
         */
        VERSION
    }
    
    /**
     * 比较策略枚举
     */
    public enum ComparisonStrategy {
        /**
         * 大于
         */
        GREATER_THAN,
        
        /**
         * 大于等于
         */
        GREATER_THAN_OR_EQUAL,
        
        /**
         * 不等于
         */
        NOT_EQUAL,
        
        /**
         * 字符串包含
         */
        STRING_CONTAINS
    }
    
    /**
     * 水印更新策略枚举
     */
    public enum WatermarkUpdateStrategy {
        /**
         * 每条记录后更新
         */
        AFTER_EACH_RECORD,
        
        /**
         * 每批记录后更新
         */
        AFTER_BATCH,
        
        /**
         * 任务完成后更新
         */
        AFTER_TASK_COMPLETION
    }
}
