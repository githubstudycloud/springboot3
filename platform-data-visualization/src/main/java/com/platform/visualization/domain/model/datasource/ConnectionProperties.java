package com.platform.visualization.domain.model.datasource;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源连接属性
 * 不同类型的数据源有不同的连接属性
 */
public class ConnectionProperties {
    private final Map<String, String> properties;

    public ConnectionProperties() {
        this.properties = new HashMap<>();
    }

    public ConnectionProperties(Map<String, String> properties) {
        this.properties = new HashMap<>(properties);
    }

    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public String getProperty(String key) {
        return this.properties.get(key);
    }

    public Map<String, String> getAllProperties() {
        return new HashMap<>(properties);
    }
}