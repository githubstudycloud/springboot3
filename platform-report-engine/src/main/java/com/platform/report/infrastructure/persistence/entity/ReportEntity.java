package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表实体
 */
@Data
@Entity
@Table(name = "report")
public class ReportEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String templateId;
    
    @Column(nullable = false, length = 50)
    private String createdBy;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ReportStatusEnum status;
    
    @Column(columnDefinition = "TEXT")
    private String parametersJson;
    
    @Column(length = 50)
    private String currentVersionId;
    
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReportVersionEntity> versions = new ArrayList<>();
    
    /**
     * 报表状态枚举
     */
    public enum ReportStatusEnum {
        GENERATING, GENERATED, FAILED
    }
}
