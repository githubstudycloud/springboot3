package com.example.platform.governance.metadata.infrastructure.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据分类MongoDB实体类
 */
@Document(collection = "metadata_categories")
public class MetadataCategoryEntity {
    
    @Id
    private String id;
    
    private String name;
    
    private String code;
    
    private String description;
    
    private String parentId;
    
    private String path;
    
    private int level;
    
    private boolean leaf;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<MetadataCategoryEntity> children = new ArrayList<>();
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public boolean isLeaf() {
        return leaf;
    }
    
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<MetadataCategoryEntity> getChildren() {
        return children;
    }
    
    public void setChildren(List<MetadataCategoryEntity> children) {
        this.children = children;
    }
    
    /**
     * 添加子分类
     * 
     * @param child 子分类
     */
    public void addChild(MetadataCategoryEntity child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        this.leaf = false;
    }
    
    /**
     * 移除子分类
     * 
     * @param childId 子分类ID
     * @return 是否成功移除
     */
    public boolean removeChild(String childId) {
        if (children == null) {
            return false;
        }
        boolean removed = children.removeIf(child -> child.getId().equals(childId));
        if (removed) {
            this.leaf = children.isEmpty();
        }
        return removed;
    }
}
