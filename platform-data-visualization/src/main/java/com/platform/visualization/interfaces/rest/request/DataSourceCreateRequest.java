package com.platform.visualization.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源创建请求
 */
public class DataSourceCreateRequest {
    
    @NotBlank(message = "数据源名称不能为空")
    private String name;
    
    private String description;
    
    @NotBlank(message = "数据源类型不能为空")
    private String type;
    
    @NotNull(message = "连接属性不能为空")
    private Map<String, String> connectionProperties = new HashMap<>();
    
    private boolean active = true;
    
    private Map<String, String> metadata = new HashMap<>();
    
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Map<String, String> getConnectionProperties() {
        return connectionProperties;
    }
    
    public void setConnectionProperties(Map<String, String> connectionProperties) {
        this.connectionProperties = connectionProperties;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}