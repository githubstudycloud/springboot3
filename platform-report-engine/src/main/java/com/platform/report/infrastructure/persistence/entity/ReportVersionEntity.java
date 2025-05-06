package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表版本实体
 */
@Data
@Entity
@Table(name = "report_version")
public class ReportVersionEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 50)
    private String reportId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TEXT")
    private String metadataJson;
    
    @Column(nullable = false, length = 200)
    private String fileName;
    
    @Column(nullable = false, length = 50)
    private String contentType;
    
    @Column(nullable = false)
    private long contentSize;
    
    @Column(nullable = false, length = 200)
    private String storagePath;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", insertable = false, updatable = false)
    private ReportEntity report;
    
    @Transient
    private Map<String, Object> metadata = new HashMap<>();
}
