package com.example.dbmonitor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessage {
    private String alertType;
    private String dataSource;
    private String message;
    private LocalDateTime timestamp;
    private String severity;
    private List<ProcessInfo> longRunningQueries;
    private Map<String, Object> additionalInfo;
    
    // 数据源标签
    private List<String> tags;
    
    // 监控指标快照
    private AlertMetrics metrics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertMetrics {
        private Integer currentConnections;
        private Integer maxConnections;
        private Integer slowQueries;
        private Integer lockWaits;
        private Double connectionUsagePercent;
        private Long totalQueries;
    }
}