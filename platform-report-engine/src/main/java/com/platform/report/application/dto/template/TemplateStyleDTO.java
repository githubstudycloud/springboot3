package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 模板样式DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateStyleDTO {
    
    private ColorSchemeDTO colorScheme;
    private FontFamilyDTO fontFamily;
    private Map<String, String> cssVariables;
    private Map<String, Object> themeProperties;
    
    /**
     * 颜色方案DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColorSchemeDTO {
        private String primary;
        private String secondary;
        private String accent;
        private String background;
        private String text;
    }
    
    /**
     * 字体家族DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FontFamilyDTO {
        private String headings;
        private String body;
        private String monospace;
    }
}
