package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模板详情DTO
 * 包含模板的完整详细信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDetailDTO {
    
    private String id;
    private String name;
    private String description;
    private String templateType;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String dataSourceId;
    private String dataSourceName;
    private String dataSetId;
    private String dataSetName;
    
    private TemplateLayoutDTO layout;
    private TemplateStyleDTO style;
    private List<TemplateComponentDTO> components;
    private List<TemplateParameterDTO> parameters;
}
