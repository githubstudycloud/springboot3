package com.example.platform.governance.core.domain.model.asset;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 数据资产领域模型
 * 
 * 代表系统中的一个数据资产，作为数据治理的核心对象
 * 数据资产可以是数据表、API、文件等各种形式的数据
 */
public class DataAsset {
    
    private final String id;
    private String name;
    private String code;
    private String description;
    private DataAssetType type;
    private DataAssetStatus status;
    private String ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Tag> tags;
    private Set<DataField> fields;
    
    /**
     * 创建新的数据资产
     * 
     * @param name 资产名称
     * @param code 资产编码
     * @param type 资产类型
     * @param description 资产描述
     * @return 新创建的数据资产实例
     */
    public static DataAsset create(String name, String code, DataAssetType type, String description) {
        DataAsset asset = new DataAsset();
        asset.id = UUID.randomUUID().toString();
        asset.name = name;
        asset.code = code;
        asset.type = type;
        asset.description = description;
        asset.status = DataAssetStatus.DRAFT;
        asset.createdAt = LocalDateTime.now();
        asset.updatedAt = LocalDateTime.now();
        asset.tags = new HashSet<>();
        asset.fields = new HashSet<>();
        
        return asset;
    }
    
    /**
     * 添加字段到数据资产
     * 
     * @param field 要添加的字段
     */
    public void addField(DataField field) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        
        // 检查字段编码是否已存在
        boolean fieldExists = fields.stream()
                .anyMatch(existingField -> existingField.getCode().equals(field.getCode()));
                
        if (fieldExists) {
            throw new IllegalArgumentException("Field with code " + field.getCode() + " already exists");
        }
        
        fields.add(field);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除字段
     * 
     * @param fieldCode 要移除的字段编码
     * @return 是否成功移除
     */
    public boolean removeField(String fieldCode) {
        boolean removed = fields.removeIf(field -> field.getCode().equals(fieldCode));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    /**
     * 添加标签
     * 
     * @param tag 要添加的标签
     */
    public void addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null");
        }
        tags.add(tag);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除标签
     * 
     * @param tagName 要移除的标签名称
     * @return 是否成功移除
     */
    public boolean removeTag(String tagName) {
        boolean removed = tags.removeIf(tag -> tag.getName().equals(tagName));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    /**
     * 设置资产状态
     * 
     * @param newStatus 新状态
     */
    public void setStatus(DataAssetStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        // 状态转换验证逻辑
        if (this.status == DataAssetStatus.ARCHIVED && newStatus != DataAssetStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot change status from ARCHIVED");
        }
        
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置资产所有者
     * 
     * @param ownerId 所有者ID
     * @param ownerName 所有者名称
     */
    public void setOwner(String ownerId, String ownerName) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner ID cannot be null or empty");
        }
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be null or empty");
        }
        
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新资产的基本信息
     * 
     * @param name 新名称
     * @param description 新描述
     */
    public void updateBasicInfo(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
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
    
    public DataAssetType getType() {
        return type;
    }
    
    public DataAssetStatus getStatus() {
        return status;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }
    
    public Set<DataField> getFields() {
        return Collections.unmodifiableSet(fields);
    }
    
    // 私有构造函数，强制使用工厂方法
    private DataAsset() {
    }
}
