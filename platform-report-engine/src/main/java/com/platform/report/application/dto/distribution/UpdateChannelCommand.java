package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 更新渠道命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChannelCommand {
    
    private String distributionId;
    private String channelId;
    private Map<String, Object> properties;
    private String operatedBy;
}
