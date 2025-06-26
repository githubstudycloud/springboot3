package com.example.dbmonitor.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MonitorResult {
    private String dataSourceName;
    private LocalDateTime timestamp;
    private boolean healthy;
    private List<ProcessInfo> longRunningQueries;
    private Map<String, Object> metrics;
    private List<String> issues;
    private long checkDuration; // 检查耗时（毫秒）
}