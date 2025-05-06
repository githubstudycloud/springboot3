package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报表生成状态DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationStatusDTO {
    
    private String reportId;
    private String status;
    private int progress;
    private String message;
    private Long estimatedTimeRemaining;
    private Long startTimestamp;
    private Long endTimestamp;
}
