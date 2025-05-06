package com.platform.report.domain.model.distribution;

import com.platform.report.domain.model.common.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 报表分发聚合根
 * 定义报表分发的配置和状态
 */
@Getter
public class ReportDistribution implements AggregateRoot<String> {
    
    private final String id;
    private String name;
    private String reportId;
    private String reportVersionId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private DistributionStatus status;
    private List<DistributionChannel> channels;
    private LocalDateTime lastDistributionTime;
    private String comment;
    
    /**
     * 创建新的报表分发
     */
    public static ReportDistribution create(String name, String reportId, String reportVersionId, 
                                         String createdBy, String comment) {
        ReportDistribution distribution = new ReportDistribution();
        distribution.name = name;
        distribution.reportId = reportId;
        distribution.reportVersionId = reportVersionId;
        distribution.createdBy = createdBy;
        distribution.createdAt = LocalDateTime.now();
        distribution.updatedAt = LocalDateTime.now();
        distribution.status = DistributionStatus.PENDING;
        distribution.channels = new ArrayList<>();
        distribution.comment = comment;
        
        return distribution;
    }
    
    /**
     * 添加分发渠道
     */
    public void addChannel(DistributionChannel channel) {
        this.channels.add(channel);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除分发渠道
     */
    public void removeChannel(String channelId) {
        this.channels.removeIf(channel -> channel.getId().equals(channelId));
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记分发开始
     */
    public void markAsInProgress() {
        if (this.status == DistributionStatus.PENDING) {
            this.status = DistributionStatus.IN_PROGRESS;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 标记分发完成
     */
    public void markAsCompleted() {
        if (this.status == DistributionStatus.IN_PROGRESS) {
            this.status = DistributionStatus.COMPLETED;
            this.lastDistributionTime = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 标记分发失败
     */
    public void markAsFailed(String errorMessage) {
        this.status = DistributionStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
        // 可以在这里记录错误信息
    }
    
    /**
     * 重试分发
     */
    public void retry() {
        if (this.status == DistributionStatus.FAILED) {
            this.status = DistributionStatus.PENDING;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    // 私有构造函数
    private ReportDistribution() {
        this.id = UUID.randomUUID().toString();
        this.channels = new ArrayList<>();
    }
}
