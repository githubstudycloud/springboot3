package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报表详情DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailDTO {
    
    private String id;
    private String name;
    private String templateId;
    private String templateName;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private Map<String, Object> parameters;
    private List<ReportVersionDTO> versions;
    private ReportVersionDTO currentVersion;
}
