package com.example.dbmonitor.entity;

import lombok.Data;

@Data
public class ProcessInfo {
    private Long id;
    private String user;
    private String host;
    private String db;
    private String command;
    private Long time;
    private String state;
    private String info;
    
    // 额外的诊断信息
    private Long rowsSent;
    private Long rowsExamined;
    
    public boolean isLongRunning(int threshold) {
        return time != null && time > threshold && 
               command != null && "Query".equals(command) &&
               info != null && !info.isEmpty();
    }
    
    public String getSqlPreview() {
        if (info == null) return "";
        // 返回SQL的前100个字符
        return info.length() > 100 ? info.substring(0, 100) + "..." : info;
    }
    
    public String getSeverity(int warningThreshold, int criticalThreshold) {
        if (time == null) return "normal";
        if (time >= criticalThreshold) return "critical";
        if (time >= warningThreshold) return "warning";
        return "normal";
    }
}