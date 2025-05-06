package com.platform.visualization.application.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源数据传输对象
 */
public class DataSourceDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private Map<String, String> connectionProperties;
    private boolean active;
    private Map<String, String> metadata;

    public DataSourceDTO() {
        this.connectionProperties = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    // Getter方法
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getConnectionProperties() {
        return connectionProperties;
    }

    public boolean isActive() {
        return active;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    // Setter方法
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConnectionProperties(Map<String, String> connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}