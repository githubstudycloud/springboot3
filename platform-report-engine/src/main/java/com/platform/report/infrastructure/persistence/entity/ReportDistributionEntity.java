package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表分发实体
 */
@Data
@Entity
@Table(name = "report_distribution")
public class ReportDistributionEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String reportId;
    
    @Column(length = 50)
    private String reportVersionId;
    
    @Column(nullable = false, length = 50)
    private String createdBy;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DistributionStatusEnum status;
    
    @Column
    private LocalDateTime lastDistributionTime;
    
    @Column(length = 500)
    private String comment;
    
    @OneToMany(mappedBy = "distribution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DistributionChannelEntity> channels = new ArrayList<>();
    
    /**
     * 分发状态枚举
     */
    public enum DistributionStatusEnum {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}
