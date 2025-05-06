package com.platform.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 分发渠道实体
 */
@Data
@Entity
@Table(name = "distribution_channel")
public class DistributionChannelEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ChannelTypeEnum type;
    
    @Column(columnDefinition = "TEXT")
    private String propertiesJson;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ChannelStatusEnum status;
    
    @Column(length = 500)
    private String lastError;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distribution_id")
    private ReportDistributionEntity distribution;
    
    @Transient
    private Map<String, Object> properties = new HashMap<>();
    
    /**
     * 渠道类型枚举
     */
    public enum ChannelTypeEnum {
        EMAIL, SMS, WECHAT, APP_PUSH, FTP, SHARED_FOLDER, API_CALLBACK
    }
    
    /**
     * 渠道状态枚举
     */
    public enum ChannelStatusEnum {
        ACTIVE, INACTIVE
    }
}
