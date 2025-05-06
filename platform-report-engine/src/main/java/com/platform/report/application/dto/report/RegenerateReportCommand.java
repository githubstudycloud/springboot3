package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 重新生成报表命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegenerateReportCommand {
    
    private String reportId;
    private Map<String, Object> parameters;
    private String operatedBy;
}
