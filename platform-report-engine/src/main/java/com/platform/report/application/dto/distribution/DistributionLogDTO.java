package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分发日志DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionLogDTO {
    
    private String id;
    private String distributionId;
    private String channelId;
    private String channelType;
    private LocalDateTime timestamp;
    private String status;
    private String message;
    private String operatedBy;
}
