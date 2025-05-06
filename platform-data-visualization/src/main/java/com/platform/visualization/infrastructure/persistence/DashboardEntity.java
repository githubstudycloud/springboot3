package com.platform.visualization.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 仪表板持久化实体
 */
@Entity
@Table(name = "viz_dashboard")
public class DashboardEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DashboardItemEntity> items = new HashSet<>();
    
    @Column(name = "layout_type")
    private String layoutType;
    
    @Column
    private Integer columns;
    
    @Column(name = "row_height")
    private Integer rowHeight;
    
    @Column
    private Integer margin;
    
    @Column
    private String theme;
    
    @Column(name = "is_public")
    private boolean isPublic;
    
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

    public Set<DashboardItemEntity> getItems() {
        return items;
    }

    public void setItems(Set<DashboardItemEntity> items) {
        this.items = items;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(String layoutType) {
        this.layoutType = layoutType;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(Integer rowHeight) {
        this.rowHeight = rowHeight;
    }

    public Integer getMargin() {
        return margin;
    }

    public void setMargin(Integer margin) {
        this.margin = margin;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}