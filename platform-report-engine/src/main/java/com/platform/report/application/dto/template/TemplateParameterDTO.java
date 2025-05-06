package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板参数DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateParameterDTO {
    
    private String name;
    private String type;
    private boolean required;
    private Object defaultValue;
    private String label;
    private String description;
}
