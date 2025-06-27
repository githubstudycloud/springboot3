package com.example.dbmonitor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报警请求实体 - 符合新的三参数格式
 * receiver, auth, content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {
    
    /**
     * 接收方 - 根据级别设置（info-alerts, warn-alerts, error-alerts）
     */
    private String receiver;
    
    /**
     * 认证token - 从配置文件获取对应级别的token
     */
    private String auth;
    
    /**
     * 报警内容 - 格式化后的报警消息
     */
    private String content;
    
    /**
     * 扩展信息（非必须，用于调试和记录）
     */
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();
    
    @Builder.Default
    private String severity = "info";
    
    private String dataSource;
}