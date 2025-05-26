package com.framework.excel.entity;

/**
 * 模型信息实体类
 *
 * @author Framework
 * @since 1.0.0
 */
public class Model extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 模型ID
     */
    private Long id;
    
    /**
     * 模型编码
     */
    private String code;
    
    /**
     * 模型名称
     */
    private String name;
    
    /**
     * 分类ID（可选）
     */
    private Long categoryId;
    
    /**
     * 分类名称（关联字段）
     */
    private String categoryName;
    
    /**
     * 型号
     */
    private String model;
    
    /**
     * 规格
     */
    private String specification;
    
    /**
     * 品牌
     */
    private String brand;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
}
