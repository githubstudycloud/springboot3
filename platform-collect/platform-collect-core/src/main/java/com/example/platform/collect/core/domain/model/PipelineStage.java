package com.example.platform.collect.core.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 流水线阶段模型
 * 定义采集流水线中的单个处理阶段
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PipelineStage {
    
    /**
     * 阶段标识
     */
    private String id;
    
    /**
     * 阶段名称
     */
    private String name;
    
    /**
     * 阶段类型
     */
    private StageType type;
    
    /**
     * 阶段配置参数
     */
    private Map<String, Object> config;
    
    /**
     * 阶段处理器类型
     * 指定实现此阶段的处理器类型
     */
    private String processorType;
    
    /**
     * 是否启用
     */
    private boolean enabled;
    
    /**
     * 是否是必需阶段
     * 必需阶段失败会导致整个任务失败
     */
    private boolean required;
    
    /**
     * 超时时间（秒）
     */
    private int timeoutSeconds;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 构造函数
     */
    @Builder
    public PipelineStage(String id, String name, StageType type, Map<String, Object> config, 
                        String processorType, Boolean enabled, Boolean required, 
                        Integer timeoutSeconds, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.config = config != null ? config : new HashMap<>();
        this.processorType = processorType;
        this.enabled = enabled != null ? enabled : true;
        this.required = required != null ? required : true;
        this.timeoutSeconds = timeoutSeconds != null ? timeoutSeconds : 300; // 默认5分钟
        this.description = description;
    }
    
    /**
     * 添加配置参数
     *
     * @param key 参数键
     * @param value 参数值
     */
    public void addConfig(String key, Object value) {
        if (this.config == null) {
            this.config = new HashMap<>();
        }
        this.config.put(key, value);
    }
    
    /**
     * 获取配置参数值
     *
     * @param key 参数键
     * @param <T> 参数类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String key) {
        return (T) config.get(key);
    }
    
    /**
     * 获取配置参数值，若不存在则返回默认值
     *
     * @param key 参数键
     * @param defaultValue 默认值
     * @param <T> 参数类型
     * @return 参数值或默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String key, T defaultValue) {
        Object value = config.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 启用阶段
     */
    public void enable() {
        this.enabled = true;
    }
    
    /**
     * 禁用阶段
     */
    public void disable() {
        this.enabled = false;
    }
    
    /**
     * 验证阶段配置是否有效
     *
     * @return 是否有效
     */
    public boolean validate() {
        // 基本验证：必填字段非空
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        if (type == null) {
            return false;
        }
        
        if (processorType == null || processorType.trim().isEmpty()) {
            return false;
        }
        
        // 特定类型验证可以委托给具体的处理器实现
        return true;
    }
}
