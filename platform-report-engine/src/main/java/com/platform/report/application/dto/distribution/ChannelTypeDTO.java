package com.platform.report.application.dto.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 渠道类型DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelTypeDTO {
    
    private String type;
    private String name;
    private String description;
    private String icon;
    private List<ChannelPropertyDTO> properties;
    
    /**
     * 渠道属性DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelPropertyDTO {
        private String name;
        private String label;
        private String type;
        private boolean required;
        private Object defaultValue;
        private String placeholder;
        private String description;
        private Map<String, Object> validations;
        private List<OptionDTO> options;
        
        /**
         * 选项DTO
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OptionDTO {
            private String value;
            private String label;
        }
    }
}
