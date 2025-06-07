package com.platform.config.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配置变更事件
 * 用于事件驱动的配置管理架构
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeEvent {

    /**
     * 应用名称
     */
    private String application;

    /**
     * 环境配置
     */
    private String profile;

    /**
     * 操作类型 (CREATE, UPDATE, DELETE, ROLLBACK, SYNC等)
     */
    private String operation;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 变更前的值
     */
    private String oldValue;

    /**
     * 变更后的值
     */
    private String newValue;

    /**
     * 操作人员
     */
    private String operator;

    /**
     * 事件时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 关联的版本ID
     */
    private Long versionId;

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * 事件来源 (WEB, API, SCHEDULE, SYSTEM等)
     */
    private String source;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     * 事件跟踪ID
     */
    private String traceId;

    /**
     * 创建便捷的构造方法
     */
    public static ConfigChangeEvent of(String application, String profile, String operation, 
                                     String operator, String oldValue, String newValue) {
        return ConfigChangeEvent.builder()
                .application(application)
                .profile(profile)
                .operation(operation)
                .operator(operator)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
    }

    /**
     * 创建完整参数的构造方法
     */
    public static ConfigChangeEvent of(String application, String profile, String operation,
                                     String configKey, String oldValue, String newValue, 
                                     String operator, Long versionId, String businessId) {
        return ConfigChangeEvent.builder()
                .application(application)
                .profile(profile)
                .operation(operation)
                .configKey(configKey)
                .oldValue(oldValue)
                .newValue(newValue)
                .operator(operator)
                .versionId(versionId)
                .businessId(businessId)
                .build();
    }
} 