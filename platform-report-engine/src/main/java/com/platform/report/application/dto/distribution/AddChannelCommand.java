package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 添加渠道命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddChannelCommand {
    
    private String distributionId;
    private String channelType;
    private Map<String, Object> properties;
    private String operatedBy;
}
