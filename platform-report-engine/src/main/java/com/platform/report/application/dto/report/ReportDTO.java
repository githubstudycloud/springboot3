package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报表DTO
 * 用于报表列表展示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    
    private String id;
    private String name;
    private String templateId;
    private String templateName;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private long contentSize;
    private String contentType;
    private String fileName;
    private int versionCount;
}
