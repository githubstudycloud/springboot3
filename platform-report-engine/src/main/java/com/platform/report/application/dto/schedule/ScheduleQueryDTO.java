package com.platform.report.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 计划查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleQueryDTO {
    
    private String keyword;
    private String templateId;
    private String status;
    private String createdBy;
    private String recurrenceType;
    private boolean includeCompleted;
    private int page;
    private int size;
    private String sortField;
    private String sortDirection;
}
