package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新模板样式命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemplateStyleCommand {
    
    private String templateId;
    private TemplateStyleDTO style;
    private String operatedBy;
}
