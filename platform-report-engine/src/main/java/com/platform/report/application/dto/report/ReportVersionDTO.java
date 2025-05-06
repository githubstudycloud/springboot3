package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 报表版本DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportVersionDTO {
    
    private String id;
    private String reportId;
    private LocalDateTime createdAt;
    private long contentSize;
    private String contentType;
    private String fileName;
    private Map<String, Object> metadata;
}
