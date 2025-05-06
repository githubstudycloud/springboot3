package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 分发渠道DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionChannelDTO {
    
    private String id;
    private String type;
    private String typeName;
    private Map<String, Object> properties;
    private String status;
    private String lastError;
}
