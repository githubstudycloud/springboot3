package com.example.platform.governance.metadata.interfaces.rest.dto;

/**
 * 分类创建请求对象
 */
public class CategoryCreateRequest {
    
    private String name;
    private String code;
    private String description;
    
    // Getters and Setters
    
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
}
