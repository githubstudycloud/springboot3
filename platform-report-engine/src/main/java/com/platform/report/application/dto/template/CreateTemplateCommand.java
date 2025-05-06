package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建模板命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateCommand {
    
    private String name;
    private String description;
    private String createdBy;
    private String templateType;
    private TemplateLayoutDTO layout;
    private TemplateStyleDTO style;
    private String dataSourceId;
    private String dataSetId;
}
