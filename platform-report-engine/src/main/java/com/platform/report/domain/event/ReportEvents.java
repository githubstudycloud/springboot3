package com.platform.report.domain.event;

import lombok.Getter;

/**
 * 报表事件
 */
public class ReportEvents {
    
    /**
     * 报表创建事件
     */
    @Getter
    public static class ReportCreated extends DomainEvent {
        private final String reportId;
        private final String name;
        private final String templateId;
        private final String createdBy;
        
        public ReportCreated(String reportId, String name, String templateId, String createdBy) {
            super();
            this.reportId = reportId;
            this.name = name;
            this.templateId = templateId;
            this.createdBy = createdBy;
        }
    }
    
    /**
     * 报表生成开始事件
     */
    @Getter
    public static class ReportGenerationStarted extends DomainEvent {
        private final String reportId;
        private final String name;
        
        public ReportGenerationStarted(String reportId, String name) {
            super();
            this.reportId = reportId;
            this.name = name;
        }
    }
    
    /**
     * 报表生成完成事件
     */
    @Getter
    public static class ReportGenerationCompleted extends DomainEvent {
        private final String reportId;
        private final String name;
        private final String versionId;
        private final long contentSize;
        
        public ReportGenerationCompleted(String reportId, String name, String versionId, long contentSize) {
            super();
            this.reportId = reportId;
            this.name = name;
            this.versionId = versionId;
            this.contentSize = contentSize;
        }
    }
    
    /**
     * 报表生成失败事件
     */
    @Getter
    public static class ReportGenerationFailed extends DomainEvent {
        private final String reportId;
        private final String name;
        private final String errorMessage;
        
        public ReportGenerationFailed(String reportId, String name, String errorMessage) {
            super();
            this.reportId = reportId;
            this.name = name;
            this.errorMessage = errorMessage;
        }
    }
    
    /**
     * 报表下载事件
     */
    @Getter
    public static class ReportDownloaded extends DomainEvent {
        private final String reportId;
        private final String versionId;
        private final String downloadedBy;
        
        public ReportDownloaded(String reportId, String versionId, String downloadedBy) {
            super();
            this.reportId = reportId;
            this.versionId = versionId;
            this.downloadedBy = downloadedBy;
        }
    }
    
    /**
     * 报表删除事件
     */
    @Getter
    public static class ReportDeleted extends DomainEvent {
        private final String reportId;
        private final String name;
        private final String deletedBy;
        
        public ReportDeleted(String reportId, String name, String deletedBy) {
            super();
            this.reportId = reportId;
            this.name = name;
            this.deletedBy = deletedBy;
        }
    }
}
