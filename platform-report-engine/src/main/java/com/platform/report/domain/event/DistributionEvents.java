package com.platform.report.domain.event;

import lombok.Getter;

/**
 * 分发事件
 */
public class DistributionEvents {
    
    /**
     * 分发创建事件
     */
    @Getter
    public static class DistributionCreated extends DomainEvent {
        private final String distributionId;
        private final String name;
        private final String reportId;
        private final String createdBy;
        
        public DistributionCreated(String distributionId, String name, String reportId, String createdBy) {
            super();
            this.distributionId = distributionId;
            this.name = name;
            this.reportId = reportId;
            this.createdBy = createdBy;
        }
    }
    
    /**
     * 分发开始事件
     */
    @Getter
    public static class DistributionStarted extends DomainEvent {
        private final String distributionId;
        private final String name;
        private final int channelCount;
        
        public DistributionStarted(String distributionId, String name, int channelCount) {
            super();
            this.distributionId = distributionId;
            this.name = name;
            this.channelCount = channelCount;
        }
    }
    
    /**
     * 分发完成事件
     */
    @Getter
    public static class DistributionCompleted extends DomainEvent {
        private final String distributionId;
        private final String name;
        private final int successCount;
        private final int failCount;
        
        public DistributionCompleted(String distributionId, String name, int successCount, int failCount) {
            super();
            this.distributionId = distributionId;
            this.name = name;
            this.successCount = successCount;
            this.failCount = failCount;
        }
    }
    
    /**
     * 分发失败事件
     */
    @Getter
    public static class DistributionFailed extends DomainEvent {
        private final String distributionId;
        private final String name;
        private final String errorMessage;
        
        public DistributionFailed(String distributionId, String name, String errorMessage) {
            super();
            this.distributionId = distributionId;
            this.name = name;
            this.errorMessage = errorMessage;
        }
    }
    
    /**
     * 渠道分发成功事件
     */
    @Getter
    public static class ChannelDistributionSucceeded extends DomainEvent {
        private final String distributionId;
        private final String channelId;
        private final String channelType;
        
        public ChannelDistributionSucceeded(String distributionId, String channelId, String channelType) {
            super();
            this.distributionId = distributionId;
            this.channelId = channelId;
            this.channelType = channelType;
        }
    }
    
    /**
     * 渠道分发失败事件
     */
    @Getter
    public static class ChannelDistributionFailed extends DomainEvent {
        private final String distributionId;
        private final String channelId;
        private final String channelType;
        private final String errorMessage;
        
        public ChannelDistributionFailed(String distributionId, String channelId, String channelType, String errorMessage) {
            super();
            this.distributionId = distributionId;
            this.channelId = channelId;
            this.channelType = channelType;
            this.errorMessage = errorMessage;
        }
    }
    
    /**
     * 分发重试事件
     */
    @Getter
    public static class DistributionRetried extends DomainEvent {
        private final String distributionId;
        private final String name;
        private final String retriedBy;
        
        public DistributionRetried(String distributionId, String name, String retriedBy) {
            super();
            this.distributionId = distributionId;
            this.name = name;
            this.retriedBy = retriedBy;
        }
    }
}
