package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表模板实体
 */
@Data
@Entity
@Table(name = "report_template")
public class ReportTemplateEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, length = 50)
    private String createdBy;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TemplateTypeEnum templateType;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TemplateStatusEnum status;
    
    @Column(length = 50)
    private String dataSourceId;
    
    @Column(length = 50)
    private String dataSetId;
    
    @Column(columnDefinition = "TEXT")
    private String layoutJson;
    
    @Column(columnDefinition = "TEXT")
    private String styleJson;
    
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TemplateComponentEntity> components = new ArrayList<>();
    
    /**
     * 模板类型枚举
     */
    public enum TemplateTypeEnum {
        PDF, EXCEL, WORD, HTML, IMAGE, DASHBOARD
    }
    
    /**
     * 模板状态枚举
     */
    public enum TemplateStatusEnum {
        DRAFT, PUBLISHED, DEPRECATED, ARCHIVED
    }
}
