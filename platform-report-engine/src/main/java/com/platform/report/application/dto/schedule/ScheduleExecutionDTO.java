package com.platform.report.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 计划执行DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleExecutionDTO {
    
    private String id;
    private String scheduleId;
    private String scheduleName;
    private LocalDateTime executionTime;
    private String status;
    private String reportId;
    private String reportName;
    private String errorMessage;
    private long duration;
    private String triggeredBy;
    private boolean manual;
}
