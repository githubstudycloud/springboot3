package com.platform.monitor.domain.service;

import com.platform.monitor.domain.model.Alert;
import com.platform.monitor.domain.model.AlertRule;
import com.platform.monitor.domain.model.Metric;
import com.platform.monitor.domain.model.ServiceInstance;

import java.util.List;

/**
 * 告警规则引擎领域服务接口
 */
public interface AlertRuleEngineService {
    
    /**
     * 评估指标数据是否触发告警
     * 
     * @param metric 指标数据
     * @return 触发的告警列表
     */
    List<Alert> evaluateMetric(Metric metric);
    
    /**
     * 评估服务实例的所有指标是否触发告警
     * 
     * @param serviceInstance 服务实例
     * @param metrics 指标数据列表
     * @return 触发的告警列表
     */
    List<Alert> evaluateServiceInstance(ServiceInstance serviceInstance, List<Metric> metrics);
    
    /**
     * 处理指标恢复事件
     * 
     * @param metric 指标数据
     * @return 恢复的告警列表
     */
    List<Alert> handleMetricRecovery(Metric metric);
    
    /**
     * 创建告警规则
     * 
     * @param alertRule 告警规则
     * @return 创建的告警规则
     */
    AlertRule createAlertRule(AlertRule alertRule);
    
    /**
     * 更新告警规则
     * 
     * @param alertRule 告警规则
     * @return 更新后的告警规则
     */
    AlertRule updateAlertRule(AlertRule alertRule);
    
    /**
     * 删除告警规则
     * 
     * @param ruleId 规则ID
     * @return 操作是否成功
     */
    boolean deleteAlertRule(String ruleId);
    
    /**
     * 启用告警规则
     * 
     * @param ruleId 规则ID
     * @return 更新后的告警规则
     */
    AlertRule enableAlertRule(String ruleId);
    
    /**
     * 禁用告警规则
     * 
     * @param ruleId 规则ID
     * @return 更新后的告警规则
     */
    AlertRule disableAlertRule(String ruleId);
    
    /**
     * 获取所有告警规则
     * 
     * @return 告警规则列表
     */
    List<AlertRule> getAllAlertRules();
    
    /**
     * 获取启用的告警规则
     * 
     * @return 启用的告警规则列表
     */
    List<AlertRule> getEnabledAlertRules();
    
    /**
     * 获取指定服务的告警规则
     * 
     * @param serviceName 服务名称
     * @return 告警规则列表
     */
    List<AlertRule> getAlertRulesByService(String serviceName);
    
    /**
     * 批量处理指标数据
     * 
     * @param metrics 指标数据列表
     * @return 触发的告警列表
     */
    List<Alert> batchEvaluateMetrics(List<Metric> metrics);
    
    /**
     * 测试告警规则
     * 
     * @param alertRule 告警规则
     * @param testValue 测试值
     * @return 是否会触发告警
     */
    boolean testAlertRule(AlertRule alertRule, double testValue);
}
