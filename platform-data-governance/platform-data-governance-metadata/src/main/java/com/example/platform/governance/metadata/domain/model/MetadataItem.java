package com.example.platform.governance.metadata.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 元数据项领域模型
 * 
 * 表示系统中的一个元数据项，作为元数据管理的基础对象
 */
public class MetadataItem {
    
    private final String id;
    private String name;
    private String code;
    private String description;
    private MetadataType type;
    private MetadataStatus status;
    private String ownerId;
    private String ownerName;
    private String categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, String> properties;
    
    /**
     * 创建新的元数据项
     * 
     * @param name 元数据名称
     * @param code 元数据编码
     * @param type 元数据类型
     * @return 新创建的元数据项实例
     */
    public static MetadataItem create(String name, String code, MetadataType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Metadata name cannot be null or empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Metadata code cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Metadata type cannot be null");
        }
        
        MetadataItem item = new MetadataItem();
        item.id = UUID.randomUUID().toString();
        item.name = name;
        item.code = code;
        item.type = type;
        item.status = MetadataStatus.DRAFT;
        item.createdAt = LocalDateTime.now();
        item.updatedAt = LocalDateTime.now();
        item.properties = new HashMap<>();
        
        return item;
    }
    
    /**
     * 设置元数据所有者
     * 
     * @param ownerId 所有者ID
     * @param ownerName 所有者名称
     */
    public void setOwner(String ownerId, String ownerName) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner ID cannot be null or empty");
        }
        
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置元数据分类
     * 
     * @param categoryId 分类ID
     * @param categoryName 分类名称
     */
    public void setCategory(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置元数据描述
     * 
     * @param description 描述信息
     */
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新元数据状态
     * 
     * @param status 新状态
     */
    public void updateStatus(MetadataStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加属性
     * 
     * @param key 属性键
     * @param value 属性值
     */
    public void addProperty(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Property key cannot be null or empty");
        }
        
        this.properties.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除属性
     * 
     * @param key 属性键
     * @return 是否成功移除
     */
    public boolean removeProperty(String key) {
        boolean removed = this.properties.remove(key) != null;
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    /**
     * 获取属性
     * 
     * @param key 属性键
     * @return 属性值，如果不存在则返回null
     */
    public String getProperty(String key) {
        return this.properties.get(key);
    }
    
    /**
     * 获取所有属性
     * 
     * @return 不可修改的属性Map
     */
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    // Getters
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public MetadataType getType() {
        return type;
    }
    
    public MetadataStatus getStatus() {
        return status;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // 私有构造函数，强制使用工厂方法
    private MetadataItem() {
        this.id = null;
    }
}
