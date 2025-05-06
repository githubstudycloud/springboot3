package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表计划实体
 */
@Data
@Entity
@Table(name = "report_schedule")
public class ReportScheduleEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, length = 50)
    private String templateId;
    
    @Column(nullable = false, length = 50)
    private String createdBy;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "TEXT")
    private String recurrenceJson;
    
    @Column(columnDefinition = "TEXT")
    private String parametersJson;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ScheduleStatusEnum status;
    
    @Column
    private LocalDateTime nextExecutionTime;
    
    @Column
    private LocalDateTime lastExecutionTime;
    
    @Column(length = 50)
    private String lastReportId;
    
    @Column(nullable = false)
    private boolean sendNotification;
    
    @Transient
    private Map<String, Object> parameters = new HashMap<>();
    
    /**
     * 计划状态枚举
     */
    public enum ScheduleStatusEnum {
        ACTIVE, PAUSED, CANCELLED, COMPLETED
    }
}
