package com.platform.monitor.domain.model;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务实例元数据
 */
@Getter
public class ServiceInstanceMetadata {
    
    /**
     * 元数据映射
     */
    private final Map<String, String> metadata;
    
    /**
     * 构造函数
     * 
     * @param metadata 元数据映射
     */
    public ServiceInstanceMetadata(Map<String, String> metadata) {
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    /**
     * 获取元数据值
     * 
     * @param key 元数据键
     * @return 元数据值，如果不存在则返回null
     */
    public String getMetadataValue(String key) {
        return metadata.get(key);
    }
    
    /**
     * 获取元数据值，如果不存在则返回默认值
     * 
     * @param key 元数据键
     * @param defaultValue 默认值
     * @return 元数据值，如果不存在则返回默认值
     */
    public String getMetadataValue(String key, String defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }
    
    /**
     * 添加元数据
     * 
     * @param key 元数据键
     * @param value 元数据值
     */
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }
    
    /**
     * 删除元数据
     * 
     * @param key 元数据键
     */
    public void removeMetadata(String key) {
        metadata.remove(key);
    }
    
    /**
     * 获取所有元数据
     * 
     * @return 元数据映射(不可修改)
     */
    public Map<String, String> getAllMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    /**
     * 清空所有元数据
     */
    public void clearMetadata() {
        metadata.clear();
    }
    
    /**
     * 创建空元数据
     * 
     * @return 空元数据实例
     */
    public static ServiceInstanceMetadata empty() {
        return new ServiceInstanceMetadata(Collections.emptyMap());
    }
}
