package com.example.platform.collect.core.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 数据源领域模型
 * 代表可以采集数据的来源，如API、数据库、文件系统等
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSource {
    
    /**
     * 数据源唯一标识
     */
    private String id;
    
    /**
     * 数据源名称
     */
    private String name;
    
    /**
     * 数据源类型
     */
    private DataSourceType type;
    
    /**
     * 数据源描述
     */
    private String description;
    
    /**
     * 连接配置，不同类型的数据源有不同的连接参数
     */
    private Map<String, Object> connectionConfig;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 是否启用
     */
    private boolean enabled;
    
    /**
     * 标签，用于分类和组织
     */
    private Map<String, String> tags;
    
    /**
     * 构造函数
     */
    @Builder
    public DataSource(String id, String name, DataSourceType type, String description, 
                      Map<String, Object> connectionConfig, String createdBy, 
                      Map<String, String> tags) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.description = description;
        this.connectionConfig = connectionConfig != null ? connectionConfig : new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.createdBy = createdBy;
        this.enabled = true;
        this.tags = tags != null ? tags : new HashMap<>();
    }
    
    /**
     * 更新数据源信息
     * 
     * @param name 名称
     * @param description 描述
     * @param connectionConfig 连接配置
     * @param tags 标签
     * @return 更新后的数据源
     */
    public DataSource update(String name, String description, 
                            Map<String, Object> connectionConfig, 
                            Map<String, String> tags) {
        this.name = name != null ? name : this.name;
        this.description = description != null ? description : this.description;
        
        if (connectionConfig != null) {
            this.connectionConfig.putAll(connectionConfig);
        }
        
        if (tags != null) {
            this.tags.putAll(tags);
        }
        
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 禁用数据源
     * 
     * @return 更新后的数据源
     */
    public DataSource disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 启用数据源
     * 
     * @return 更新后的数据源
     */
    public DataSource enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 验证数据源配置是否有效
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
        
        // 特定类型验证可以委托给具体的连接器实现
        return true;
    }
    
    /**
     * 获取连接配置中的特定参数
     * 
     * @param key 参数键
     * @param <T> 参数类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigParam(String key) {
        return (T) connectionConfig.get(key);
    }
    
    /**
     * 设置连接配置参数
     * 
     * @param key 参数键
     * @param value 参数值
     */
    public void setConfigParam(String key, Object value) {
        connectionConfig.put(key, value);
        updatedAt = LocalDateTime.now();
    }
}
