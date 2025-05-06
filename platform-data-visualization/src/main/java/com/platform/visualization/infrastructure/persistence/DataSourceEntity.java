package com.platform.visualization.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源持久化实体
 */
@Entity
@Table(name = "viz_data_source")
public class DataSourceEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String type;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "viz_data_source_properties", 
                    joinColumns = @JoinColumn(name = "data_source_id"))
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value", length = 1000)
    private Map<String, String> connectionProperties = new HashMap<>();
    
    @Column(nullable = false)
    private boolean active;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "viz_data_source_metadata", 
                    joinColumns = @JoinColumn(name = "data_source_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value", length = 1000)
    private Map<String, String> metadata = new HashMap<>();
    
    @Version
    private Long version;
    
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}