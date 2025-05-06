package com.platform.report.domain.model.report;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 报表版本
 * 记录报表的每个生成版本
 */
@Getter
public class ReportVersion {
    
    private final String id;
    private final String reportId;
    private final LocalDateTime createdAt;
    private final ReportContent content;
    private final Map<String, Object> metadata;
    
    /**
     * 创建新的报表版本
     */
    public static ReportVersion create(String reportId, ReportContent content, 
                                     Map<String, Object> metadata) {
        ReportVersion version = new ReportVersion(
            UUID.randomUUID().toString(),
            reportId,
            LocalDateTime.now(),
            content,
            metadata != null ? metadata : new HashMap<>()
        );
        return version;
    }
    
    private ReportVersion(String id, String reportId, LocalDateTime createdAt, 
                         ReportContent content, Map<String, Object> metadata) {
        this.id = id;
        this.reportId = reportId;
        this.createdAt = createdAt;
        this.content = content;
        this.metadata = metadata;
    }
}
