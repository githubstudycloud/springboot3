package com.framework.excel.entity;

/**
 * 故障信息实体类
 *
 * @author Framework
 * @since 1.0.0
 */
public class Fault extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 故障ID
     */
    private Long id;
    
    /**
     * 故障编码
     */
    private String code;
    
    /**
     * 故障名称
     */
    private String name;
    
    /**
     * 故障分类ID
     */
    private Long classificationId;
    
    /**
     * 故障分类名称（关联字段）
     */
    private String classificationName;
    
    /**
     * 故障描述
     */
    private String description;
    
    /**
     * 处理建议
     */
    private String suggestion;
    
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
    
    public Long getClassificationId() {
        return classificationId;
    }
    
    public void setClassificationId(Long classificationId) {
        this.classificationId = classificationId;
    }
    
    public String getClassificationName() {
        return classificationName;
    }
    
    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
}
