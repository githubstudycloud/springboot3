package com.platform.monitor.domain.repository;

import com.platform.monitor.domain.model.AlertRule;
import com.platform.monitor.domain.model.AlertSeverity;
import com.platform.monitor.domain.model.MetricType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 告警规则仓储接口
 */
public interface AlertRuleRepository {
    
    /**
     * 保存告警规则
     *
     * @param alertRule 告警规则
     * @return 保存后的告警规则
     */
    AlertRule save(AlertRule alertRule);
    
    /**
     * 根据ID查找告警规则
     *
     * @param id 告警规则ID
     * @return 告警规则可选结果
     */
    Optional<AlertRule> findById(String id);
    
    /**
     * 获取所有告警规则
     *
     * @return 所有告警规则列表
     */
    List<AlertRule> findAll();
    
    /**
     * 获取所有启用的告警规则
     *
     * @return 启用的告警规则列表
     */
    List<AlertRule> findAllEnabled();
    
    /**
     * 根据指标类型查找告警规则
     *
     * @param metricType 指标类型
     * @return 告警规则列表
     */
    List<AlertRule> findByMetricType(MetricType metricType);
    
    /**
     * 根据告警级别查找告警规则
     *
     * @param severity 告警级别
     * @return 告警规则列表
     */
    List<AlertRule> findBySeverity(AlertSeverity severity);
    
    /**
     * 根据服务名称查找告警规则
     *
     * @param serviceName 服务名称
     * @return 告警规则列表
     */
    List<AlertRule> findByServiceName(String serviceName);
    
    /**
     * 根据标签查找告警规则
     *
     * @param tag 标签
     * @return 告警规则列表
     */
    List<AlertRule> findByTag(String tag);
    
    /**
     * 根据多个标签查找告警规则
     *
     * @param tags 标签集合
     * @return 告警规则列表
     */
    List<AlertRule> findByTags(Set<String> tags);
    
    /**
     * 删除告警规则
     *
     * @param id 告警规则ID
     */
    void deleteById(String id);
    
    /**
     * 根据服务名称获取启用的告警规则
     *
     * @param serviceName 服务名称
     * @return 启用的告警规则列表
     */
    List<AlertRule> findEnabledByServiceName(String serviceName);
    
    /**
     * 根据指标类型获取启用的告警规则
     *
     * @param metricType 指标类型
     * @return 启用的告警规则列表
     */
    List<AlertRule> findEnabledByMetricType(MetricType metricType);
    
    /**
     * 根据通知渠道ID查找使用该通知渠道的告警规则
     *
     * @param notificationChannelId 通知渠道ID
     * @return 告警规则列表
     */
    List<AlertRule> findByNotificationChannel(String notificationChannelId);
    
    /**
     * 获取告警规则数量
     *
     * @return 告警规则数量
     */
    long count();
    
    /**
     * 获取启用的告警规则数量
     *
     * @return 启用的告警规则数量
     */
    long countEnabled();
}
