package com.platform.report.domain.model.report;

import com.platform.report.domain.model.common.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 报表实例聚合根
 * 表示一个已生成的报表
 */
@Getter
public class Report implements AggregateRoot<String> {
    
    private final String id;
    private String name;
    private String templateId;
    private String createdBy;
    private LocalDateTime createdAt;
    private ReportStatus status;
    private Map<String, Object> parameters;
    private List<ReportVersion> versions;
    private ReportVersion currentVersion;
    
    /**
     * 创建新的报表实例
     */
    public static Report create(String name, String templateId, String createdBy, 
                              Map<String, Object> parameters) {
        Report report = new Report();
        report.name = name;
        report.templateId = templateId;
        report.createdBy = createdBy;
        report.createdAt = LocalDateTime.now();
        report.status = ReportStatus.GENERATING;
        report.parameters = parameters != null ? parameters : new HashMap<>();
        report.versions = new ArrayList<>();
        
        return report;
    }
    
    /**
     * 添加新版本
     */
    public void addVersion(ReportContent content, Map<String, Object> metadata) {
        ReportVersion version = ReportVersion.create(this.id, content, metadata);
        this.versions.add(version);
        this.currentVersion = version;
        this.status = ReportStatus.GENERATED;
    }
    
    /**
     * 标记为生成失败
     */
    public void markAsFailed(String errorMessage) {
        this.status = ReportStatus.FAILED;
        // 可以在这里记录错误信息
    }
    
    /**
     * 获取指定版本
     */
    public ReportVersion getVersion(String versionId) {
        return this.versions.stream()
            .filter(v -> v.getId().equals(versionId))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    // 私有构造函数
    private Report() {
        this.id = UUID.randomUUID().toString();
        this.parameters = new HashMap<>();
        this.versions = new ArrayList<>();
    }
    
    /**
     * 报表状态枚举
     */
    public enum ReportStatus {
        GENERATING, // 生成中
        GENERATED,  // 已生成
        FAILED      // 生成失败
    }
}
