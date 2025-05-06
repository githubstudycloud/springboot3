package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报表查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportQueryDTO {
    
    private String keyword;
    private String templateId;
    private String status;
    private String createdBy;
    private String dateFrom;
    private String dateTo;
    private int page;
    private int size;
    private String sortField;
    private String sortDirection;
}
