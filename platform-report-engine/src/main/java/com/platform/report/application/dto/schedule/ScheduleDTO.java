package com.platform.report.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报表计划DTO
 * 用于计划列表展示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    
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
    private String recurrenceType;
    private String recurrenceDescription;
    private boolean sendNotification;
}
