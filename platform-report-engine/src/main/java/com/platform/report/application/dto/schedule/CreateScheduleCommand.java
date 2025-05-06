package com.platform.report.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 创建计划命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleCommand {
    
    private String name;
    private String description;
    private String templateId;
    private String createdBy;
    private RecurrenceDTO recurrence;
    private Map<String, Object> parameters;
    private boolean sendNotification;
}
