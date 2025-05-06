package com.platform.report.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 更新计划命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleCommand {
    
    private String scheduleId;
    private String name;
    private String description;
    private RecurrenceDTO recurrence;
    private Map<String, Object> parameters;
    private boolean sendNotification;
    private String operatedBy;
}
