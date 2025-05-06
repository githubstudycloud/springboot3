package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateQueryDTO {
    
    private String keyword;
    private String templateType;
    private String status;
    private String createdBy;
    private String dataSourceId;
    private String dataSetId;
    private int page;
    private int size;
    private String sortField;
    private String sortDirection;
}
