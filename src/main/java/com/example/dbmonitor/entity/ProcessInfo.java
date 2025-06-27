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
    
    /**
     * 判断是否为长时间运行的SQL
     * 只有满足以下条件的进程才会被认为是长时间运行：
     * 1. 运行时间超过阈值
     * 2. 命令类型为Query
     * 3. info字段有有效内容（非空且非空白）
     */
    public boolean isLongRunning(int threshold) {
        return time != null && time > threshold && 
               command != null && "Query".equals(command) &&
               hasValidInfo();
    }
    
    /**
     * 检查info字段是否有有效内容
     * 只有info字段不为null、不为空字符串、且去除空白后不为空的进程才会被推送
     */
    public boolean hasValidInfo() {
        return info != null && !info.trim().isEmpty() && 
               !info.trim().equalsIgnoreCase("NULL") &&
               !info.trim().equals("") &&
               info.trim().length() > 0;
    }
    
    /**
     * 获取SQL预览，用于日志记录和报警
     * 如果info字段为空，返回空字符串
     */
    public String getSqlPreview() {
        if (!hasValidInfo()) return "";
        // 返回SQL的前100个字符
        return info.length() > 100 ? info.substring(0, 100) + "..." : info;
    }
    
    /**
     * 根据运行时间获取严重程度
     */
    public String getSeverity(int warningThreshold, int criticalThreshold) {
        if (time == null) return "normal";
        if (time >= criticalThreshold) return "critical";
        if (time >= warningThreshold) return "warning";
        return "normal";
    }
    
    /**
     * 获取格式化的运行时间描述
     */
    public String getFormattedRunTime() {
        if (time == null) return "未知";
        
        if (time >= 3600) {
            return String.format("%.1f小时", time / 3600.0);
        } else if (time >= 60) {
            return String.format("%d分钟", time / 60);
        } else {
            return String.format("%d秒", time);
        }
    }
}