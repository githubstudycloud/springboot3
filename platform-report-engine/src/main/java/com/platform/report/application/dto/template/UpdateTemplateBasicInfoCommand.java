package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新模板基本信息命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemplateBasicInfoCommand {
    
    private String templateId;
    private String name;
    private String description;
    private String operatedBy;
}
