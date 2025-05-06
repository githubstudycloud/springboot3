package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建分发命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDistributionCommand {
    
    private String name;
    private String reportId;
    private String reportVersionId;
    private String createdBy;
    private String comment;
}
