package com.platform.visualization.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 图表持久化实体
 */
@Entity
@Table(name = "viz_chart")
public class ChartEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_set_id", nullable = false)
    private DataSetEntity dataSet;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "viz_chart_options", 
                    joinColumns = @JoinColumn(name = "chart_id"))
    @MapKeyColumn(name = "option_key")
    @Column(name = "option_value", length = 1000)
    private Map<String, String> options = new HashMap<>();
    
    @OneToMany(mappedBy = "chart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChartDimensionEntity> dimensions = new HashSet<>();
    
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

    public DataSetEntity getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSetEntity dataSet) {
        this.dataSet = dataSet;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Set<ChartDimensionEntity> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Set<ChartDimensionEntity> dimensions) {
        this.dimensions = dimensions;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}