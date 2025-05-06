package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 计划执行记录实体
 */
@Data
@Entity
@Table(name = "schedule_execution")
public class ScheduleExecutionEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 50)
    private String scheduleId;
    
    @Column(nullable = false)
    private LocalDateTime executionTime;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ExecutionStatusEnum status;
    
    @Column(length = 50)
    private String reportId;
    
    @Column(length = 500)
    private String errorMessage;
    
    @Column(nullable = false)
    private long duration;
    
    @Column(length = 50)
    private String triggeredBy;
    
    @Column(nullable = false)
    private boolean manual;
    
    /**
     * 执行状态枚举
     */
    public enum ExecutionStatusEnum {
        SUCCESS, FAILED, SKIPPED
    }
}
