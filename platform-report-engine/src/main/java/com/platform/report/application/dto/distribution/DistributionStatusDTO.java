package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分发状态DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionStatusDTO {
    
    private String distributionId;
    private String status;
    private int totalChannels;
    private int completedChannels;
    private int failedChannels;
    private int pendingChannels;
    private String message;
    private Long startTimestamp;
    private Long endTimestamp;
}
