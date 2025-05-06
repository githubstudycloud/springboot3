package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新模板布局命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemplateLayoutCommand {
    
    private String templateId;
    private TemplateLayoutDTO layout;
    private String operatedBy;
}
