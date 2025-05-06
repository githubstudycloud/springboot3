package com.platform.report.application.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 模板组件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateComponentDTO {
    
    private String id;
    private String name;
    private String type;
    private PositionDTO position;
    private SizeDTO size;
    private Map<String, Object> properties;
    private String dataBinding;
    
    /**
     * 位置信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDTO {
        private int x;
        private int y;
    }
    
    /**
     * 大小信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeDTO {
        private int width;
        private int height;
    }
}
