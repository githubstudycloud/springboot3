package com.platform.report.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

/**
 * 执行周期DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurrenceDTO {
    
    private String type;
    private Integer interval;
    private String executionTime;
    private Set<DayOfWeek> daysOfWeek;
    private Integer dayOfMonth;
    private Integer monthOfYear;
    private String cronExpression;
    
    // 如果直接使用LocalTime，会自动转成字符串，这里提供一个辅助方法
    public LocalTime getExecutionTimeAsLocalTime() {
        return executionTime != null ? LocalTime.parse(executionTime) : null;
    }
}
