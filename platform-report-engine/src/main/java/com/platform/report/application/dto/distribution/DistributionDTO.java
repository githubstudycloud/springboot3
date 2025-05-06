package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分发DTO
 * 用于分发列表展示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionDTO {
    
    private String id;
    private String name;
    private String reportId;
    private String reportName;
    private String reportVersionId;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastDistributionTime;
    private String comment;
    private int channelCount;
    private List<String> channelTypes;
}
