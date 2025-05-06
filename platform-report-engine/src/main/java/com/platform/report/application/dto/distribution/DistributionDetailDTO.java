package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分发详情DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionDetailDTO {
    
    private String id;
    private String name;
    private String reportId;
    private String reportName;
    private String reportVersionId;
    private String reportVersionDescription;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastDistributionTime;
    private String comment;
    private List<DistributionChannelDTO> channels;
    private List<DistributionLogDTO> logs;
}
