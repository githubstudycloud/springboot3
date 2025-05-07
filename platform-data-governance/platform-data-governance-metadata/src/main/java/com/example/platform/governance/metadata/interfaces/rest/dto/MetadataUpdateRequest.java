package com.example.platform.governance.metadata.interfaces.rest.dto;

/**
 * 元数据更新请求对象
 */
public class MetadataUpdateRequest {
    
    private String name;
    private String description;
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
