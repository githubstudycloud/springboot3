package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 模板DTO
 * 用于模板列表展示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDTO {
    
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
    private int componentCount;
}
