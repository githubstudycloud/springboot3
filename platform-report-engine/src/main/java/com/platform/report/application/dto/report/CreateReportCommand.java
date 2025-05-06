package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 创建报表命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportCommand {
    
    private String name;
    private String templateId;
    private String createdBy;
    private Map<String, Object> parameters;
    private boolean generateImmediately;
}
