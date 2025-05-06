package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 告警规则领域模型
 */
@Getter
public class AlertRule extends AbstractEntity<String> {
    
    /**
     * 告警规则ID
     */
    private final String id;
    
    /**
     * 告警规则名称
     */
    private String name;
    
    /**
     * 告警规则描述
     */
    private String description;
    
    /**
     * 告警级别
     */
    private AlertSeverity severity;
    
    /**
     * 指标类型
     */
    private final MetricType metricType;
    
    /**
     * 告警规则条件列表
     */
    private final List<AlertRuleCondition> conditions;
    
    /**
     * 通知渠道集合
     */
    private final Set<String> notificationChannels;
    
    /**
     * 规则是否启用
     */
    private boolean enabled;
    
    /**
     * 创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 服务名称限制（如果为空则适用于所有服务）
     */
    private final Set<String> serviceNames;
    
    /**
     * 告警标签
     */
    private final Set<String> tags;
    
    /**
     * 告警恢复阈值（可用于实现迟滞，防止波动导致的频繁告警）
     */
    private Double recoveryThreshold;
    
    /**
     * 是否自动关闭已恢复的告警
     */
    private boolean autoCloseRecovered;
    
    /**
     * 构造函数
     *
     * @param id 告警规则ID
     * @param name 告警规则名称
     * @param description 告警规则描述
     * @param severity 告警级别
     * @param metricType 指标类型
     * @param enabled 是否启用
     */
    public AlertRule(String id, String name, String description, AlertSeverity severity, 
                    MetricType metricType, boolean enabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.severity = severity;
        this.metricType = metricType;
        this.enabled = enabled;
        this.conditions = new ArrayList<>();
        this.notificationChannels = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.serviceNames = new HashSet<>();
        this.tags = new HashSet<>();
        this.autoCloseRecovered = true;
    }
    
    /**
     * 添加告警规则条件
     * 
     * @param condition 告警规则条件
     * @return 当前告警规则实例
     */
    public AlertRule addCondition(AlertRuleCondition condition) {
        this.conditions.add(condition);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加通知渠道
     * 
     * @param channelId 通知渠道ID
     * @return 当前告警规则实例
     */
    public AlertRule addNotificationChannel(String channelId) {
        this.notificationChannels.add(channelId);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 移除通知渠道
     * 
     * @param channelId 通知渠道ID
     * @return 当前告警规则实例
     */
    public AlertRule removeNotificationChannel(String channelId) {
        this.notificationChannels.remove(channelId);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加应用服务名称限制
     * 
     * @param serviceName 服务名称
     * @return 当前告警规则实例
     */
    public AlertRule addServiceName(String serviceName) {
        this.serviceNames.add(serviceName);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 移除应用服务名称限制
     * 
     * @param serviceName 服务名称
     * @return 当前告警规则实例
     */
    public AlertRule removeServiceName(String serviceName) {
        this.serviceNames.remove(serviceName);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加标签
     * 
     * @param tag 标签
     * @return 当前告警规则实例
     */
    public AlertRule addTag(String tag) {
        this.tags.add(tag);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 设置恢复阈值
     * 
     * @param recoveryThreshold 恢复阈值
     * @return 当前告警规则实例
     */
    public AlertRule setRecoveryThreshold(Double recoveryThreshold) {
        this.recoveryThreshold = recoveryThreshold;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 设置是否自动关闭已恢复的告警
     * 
     * @param autoCloseRecovered 是否自动关闭已恢复的告警
     * @return 当前告警规则实例
     */
    public AlertRule setAutoCloseRecovered(boolean autoCloseRecovered) {
        this.autoCloseRecovered = autoCloseRecovered;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 启用告警规则
     * 
     * @return 当前告警规则实例
     */
    public AlertRule enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 禁用告警规则
     * 
     * @return 当前告警规则实例
     */
    public AlertRule disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 评估指标值是否触发告警
     * 
     * @param metric 指标数据
     * @return 如果触发告警则返回true，否则返回false
     */
    public boolean evaluate(Metric metric) {
        // 如果规则未启用，则不触发告警
        if (!enabled) {
            return false;
        }
        
        // 如果指标类型不匹配，则不触发告警
        if (metricType != metric.getMetricType() && metricType != MetricType.CUSTOM) {
            return false;
        }
        
        // 如果服务名称限制不为空，且指标不属于指定服务，则不触发告警
        if (!serviceNames.isEmpty()) {
            String serviceName = metric.getTagValue("service.name");
            if (serviceName == null || !serviceNames.contains(serviceName)) {
                return false;
            }
        }
        
        // 所有条件必须都满足才触发告警
        for (AlertRuleCondition condition : conditions) {
            if (!condition.evaluate(metric.getValue())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 评估指标值是否满足恢复条件
     * 
     * @param metric 指标数据
     * @return 如果满足恢复条件则返回true，否则返回false
     */
    public boolean evaluateRecovery(Metric metric) {
        // 如果没有设置恢复阈值，则直接反向评估告警条件
        if (recoveryThreshold == null) {
            return !evaluate(metric);
        }
        
        // 如果指标类型不匹配，则不满足恢复条件
        if (metricType != metric.getMetricType() && metricType != MetricType.CUSTOM) {
            return false;
        }
        
        // 根据第一个条件的类型，使用恢复阈值来判断
        if (conditions.isEmpty()) {
            return false;
        }
        
        AlertRuleCondition firstCondition = conditions.get(0);
        AlertRuleConditionType conditionType = firstCondition.getConditionType();
        
        switch (conditionType) {
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
                return metric.getValue() <= recoveryThreshold;
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
                return metric.getValue() >= recoveryThreshold;
            default:
                return !evaluate(metric);
        }
    }
    
    /**
     * 更新基本信息
     * 
     * @param name 告警规则名称
     * @param description 告警规则描述
     * @param severity 告警级别
     * @return 当前告警规则实例
     */
    public AlertRule updateBasicInfo(String name, String description, AlertSeverity severity) {
        this.name = name;
        this.description = description;
        this.severity = severity;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
}
