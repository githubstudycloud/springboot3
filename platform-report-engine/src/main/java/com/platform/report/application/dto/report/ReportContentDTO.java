package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报表内容DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportContentDTO {
    
    private byte[] content;
    private String contentType;
    private long size;
    private String fileName;
    private String reportId;
    private String versionId;
}
