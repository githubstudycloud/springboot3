package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 添加模板组件命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTemplateComponentCommand {
    
    private String templateId;
    private String name;
    private String type;
    private int x;
    private int y;
    private int width;
    private int height;
    private Map<String, Object> properties;
    private String dataBinding;
    private String operatedBy;
}
