package com.example.platform.governance.metadata.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 元数据分类领域模型
 * 
 * 表示元数据的分类层次结构
 */
public class MetadataCategory {
    
    private final String id;
    private String name;
    private String code;
    private String description;
    private String parentId;
    private String path;
    private int level;
    private boolean leaf;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MetadataCategory> children;
    
    /**
     * 创建顶级元数据分类
     * 
     * @param name 分类名称
     * @param code 分类编码
     * @return 新创建的分类实例
     */
    public static MetadataCategory createRoot(String name, String code) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Category code cannot be null or empty");
        }
        
        MetadataCategory category = new MetadataCategory();
        category.id = UUID.randomUUID().toString();
        category.name = name;
        category.code = code;
        category.level = 1;
        category.path = "/" + code;
        category.leaf = true;
        category.createdAt = LocalDateTime.now();
        category.updatedAt = LocalDateTime.now();
        category.children = new ArrayList<>();
        
        return category;
    }
    
    /**
     * 创建子分类
     * 
     * @param name 分类名称
     * @param code 分类编码
     * @param parent 父分类
     * @return 新创建的分类实例
     */
    public static MetadataCategory createChild(String name, String code, MetadataCategory parent) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Category code cannot be null or empty");
        }
        if (parent == null) {
            throw new IllegalArgumentException("Parent category cannot be null");
        }
        
        MetadataCategory category = new MetadataCategory();
        category.id = UUID.randomUUID().toString();
        category.name = name;
        category.code = code;
        category.parentId = parent.getId();
        category.level = parent.getLevel() + 1;
        category.path = parent.getPath() + "/" + code;
        category.leaf = true;
        category.createdAt = LocalDateTime.now();
        category.updatedAt = LocalDateTime.now();
        category.children = new ArrayList<>();
        
        // 父分类添加子分类并更新叶子状态
        parent.addChild(category);
        parent.leaf = false;
        
        return category;
    }
    
    /**
     * 添加子分类
     * 
     * @param child 子分类
     */
    void addChild(MetadataCategory child) {
        this.children.add(child);
        this.leaf = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除子分类
     * 
     * @param childId 子分类ID
     * @return 是否成功移除
     */
    public boolean removeChild(String childId) {
        boolean removed = this.children.removeIf(child -> child.getId().equals(childId));
        
        if (removed) {
            this.leaf = this.children.isEmpty();
            this.updatedAt = LocalDateTime.now();
        }
        
        return removed;
    }
    
    /**
     * 设置分类描述
     * 
     * @param description 描述信息
     */
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取分类ID
     * 
     * @return 分类ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * 获取分类名称
     * 
     * @return 分类名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取分类编码
     * 
     * @return 分类编码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取分类描述
     * 
     * @return 分类描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取父分类ID
     * 
     * @return 父分类ID，如果是顶级分类则返回null
     */
    public String getParentId() {
        return parentId;
    }
    
    /**
     * 获取分类路径
     * 
     * @return 分类路径，格式为"/code1/code2/..."
     */
    public String getPath() {
        return path;
    }
    
    /**
     * 获取分类层级
     * 
     * @return 分类层级，顶级分类为1
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * 判断是否为叶子节点
     * 
     * @return 是否为叶子节点
     */
    public boolean isLeaf() {
        return leaf;
    }
    
    /**
     * 获取创建时间
     * 
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * 获取更新时间
     * 
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * 获取子分类列表
     * 
     * @return 不可修改的子分类列表
     */
    public List<MetadataCategory> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    /**
     * 判断是否为顶级分类
     * 
     * @return 是否为顶级分类
     */
    public boolean isRoot() {
        return parentId == null;
    }
    
    // 私有构造函数，强制使用工厂方法
    private MetadataCategory() {
        this.id = null;
    }
}
