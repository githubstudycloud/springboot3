package com.platform.report.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 报表版本比较DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportVersionComparisonDTO {
    
    private String reportId;
    private String versionId1;
    private String versionId2;
    private List<DifferenceDTO> differences;
    
    /**
     * 差异DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifferenceDTO {
        private String field;
        private Object value1;
        private Object value2;
        private String changeType;
    }
}
