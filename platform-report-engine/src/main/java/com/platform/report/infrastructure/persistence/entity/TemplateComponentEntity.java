package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板组件实体
 */
@Data
@Entity
@Table(name = "template_component")
public class TemplateComponentEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ComponentTypeEnum type;
    
    @Column(nullable = false)
    private int positionX;
    
    @Column(nullable = false)
    private int positionY;
    
    @Column(nullable = false)
    private int width;
    
    @Column(nullable = false)
    private int height;
    
    @Column(columnDefinition = "TEXT")
    private String propertiesJson;
    
    @Column(columnDefinition = "TEXT")
    private String dataBinding;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private ReportTemplateEntity template;
    
    @Transient
    private Map<String, Object> properties = new HashMap<>();
    
    /**
     * 组件类型枚举
     */
    public enum ComponentTypeEnum {
        TABLE, CHART, TEXT, IMAGE, HEADER, FOOTER, GROUP, SUBREPORT
    }
}
