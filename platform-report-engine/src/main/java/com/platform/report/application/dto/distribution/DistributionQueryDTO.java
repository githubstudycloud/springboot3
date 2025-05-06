package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分发查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionQueryDTO {
    
    private String keyword;
    private String reportId;
    private String status;
    private String createdBy;
    private String dateFrom;
    private String dateTo;
    private String channelType;
    private int page;
    private int size;
    private String sortField;
    private String sortDirection;
}
