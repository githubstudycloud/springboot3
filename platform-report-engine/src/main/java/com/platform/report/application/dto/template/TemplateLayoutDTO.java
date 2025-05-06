package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板布局DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateLayoutDTO {
    
    private String paperSize;
    private String orientation;
    private MarginDTO margin;
    private int columns;
    private int rows;
    private String gridType;
    
    /**
     * 边距DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarginDTO {
        private int top;
        private int right;
        private int bottom;
        private int left;
    }
}
