package com.platform.report.domain.model.distribution;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 分发渠道
 * 定义报表的分发方式，如邮件、消息推送等
 */
@Getter
public class DistributionChannel {
    
    private final String id;
    private ChannelType type;
    private Map<String, Object> properties;
    private ChannelStatus status;
    
    /**
     * 创建新的分发渠道
     */
    public static DistributionChannel create(ChannelType type, Map<String, Object> properties) {
        DistributionChannel channel = new DistributionChannel();
        channel.type = type;
        channel.properties = properties != null ? properties : new HashMap<>();
        channel.status = ChannelStatus.ACTIVE;
        
        return channel;
    }
    
    /**
     * 更新属性
     */
    public void updateProperties(Map<String, Object> properties) {
        this.properties.clear();
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }
    
    /**
     * 停用渠道
     */
    public void deactivate() {
        this.status = ChannelStatus.INACTIVE;
    }
    
    /**
     * 激活渠道
     */
    public void activate() {
        this.status = ChannelStatus.ACTIVE;
    }
    
    public String getId() {
        return id;
    }
    
    // 私有构造函数
    private DistributionChannel() {
        this.id = UUID.randomUUID().toString();
        this.properties = new HashMap<>();
    }
    
    /**
     * 渠道类型枚举
     */
    public enum ChannelType {
        EMAIL,          // 电子邮件
        SMS,            // 短信
        WECHAT,         // 微信
        APP_PUSH,       // APP推送
        FTP,            // FTP上传
        SHARED_FOLDER,  // 共享文件夹
        API_CALLBACK    // API回调
    }
    
    /**
     * 渠道状态枚举
     */
    public enum ChannelStatus {
        ACTIVE,   // 活跃
        INACTIVE  // 不活跃
    }
}
