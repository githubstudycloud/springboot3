package com.platform.report.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 报表计划详情DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetailDTO {
    
    private String id;
    private String name;
    private String description;
    private String templateId;
    private String templateName;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime nextExecutionTime;
    private LocalDateTime lastExecutionTime;
    private String lastReportId;
    private RecurrenceDTO recurrence;
    private Map<String, Object> parameters;
    private boolean sendNotification;
    private int totalExecutions;
    private int successfulExecutions;
    private int failedExecutions;
}
