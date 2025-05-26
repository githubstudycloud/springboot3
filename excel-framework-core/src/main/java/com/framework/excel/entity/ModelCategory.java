package com.framework.excel.entity;

/**
 * 模型分类实体类
 *
 * @author Framework
 * @since 1.0.0
 */
public class ModelCategory extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类编码
     */
    private String code;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 父级ID
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sort;
    
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
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public Integer getSort() {
        return sort;
    }
    
    public void setSort(Integer sort) {
        this.sort = sort;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
}
