package com.example.platform.governance.core.domain.model.asset;

import java.util.Objects;

/**
 * 标签领域模型
 * 
 * 用于对数据资产进行分类和标记
 */
public class Tag {
    
    private final String name;
    private String category;
    private String color;
    
    /**
     * 创建新标签
     * 
     * @param name 标签名称
     * @return 新创建的标签实例
     */
    public static Tag create(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
        
        return new Tag(name.trim());
    }
    
    /**
     * 创建带分类的标签
     * 
     * @param name 标签名称
     * @param category 标签分类
     * @return 新创建的标签实例
     */
    public static Tag create(String name, String category) {
        Tag tag = create(name);
        tag.category = category;
        return tag;
    }
    
    /**
     * 设置标签颜色
     * 
     * @param color 颜色（通常为十六进制RGB值）
     * @return 当前标签实例，支持链式调用
     */
    public Tag withColor(String color) {
        this.color = color;
        return this;
    }
    
    /**
     * 获取标签名称
     * 
     * @return 标签名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取标签分类
     * 
     * @return 标签分类
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * 获取标签颜色
     * 
     * @return 标签颜色
     */
    public String getColor() {
        return color;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return category != null ? category + ":" + name : name;
    }
    
    // 私有构造函数，强制使用工厂方法
    private Tag(String name) {
        this.name = name;
    }
}
