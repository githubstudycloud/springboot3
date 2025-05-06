package com.platform.monitor.application.service;

import com.platform.monitor.application.dto.AlertDTO;
import com.platform.monitor.application.dto.AlertEventDTO;
import com.platform.monitor.application.dto.AlertRuleDTO;
import com.platform.monitor.application.dto.AlertRuleConditionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警管理应用服务接口
 */
public interface AlertManagementAppService {
    
    /**
     * 创建告警规则
     *
     * @param alertRuleDTO 告警规则DTO
     * @return 创建的告警规则DTO
     */
    AlertRuleDTO createAlertRule(AlertRuleDTO alertRuleDTO);
    
    /**
     * 更新告警规则
     *
     * @param alertRuleDTO 告警规则DTO
     * @return 更新后的告警规则DTO
     */
    AlertRuleDTO updateAlertRule(AlertRuleDTO alertRuleDTO);
    
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
     * @return 更新后的告警规则DTO
     */
    AlertRuleDTO enableAlertRule(String ruleId);
    
    /**
     * 禁用告警规则
     *
     * @param ruleId 规则ID
     * @return 更新后的告警规则DTO
     */
    AlertRuleDTO disableAlertRule(String ruleId);
    
    /**
     * 获取告警规则
     *
     * @param ruleId 规则ID
     * @return 告警规则DTO
     */
    AlertRuleDTO getAlertRule(String ruleId);
    
    /**
     * 获取所有告警规则
     *
     * @return 告警规则DTO列表
     */
    List<AlertRuleDTO> getAllAlertRules();
    
    /**
     * 获取启用的告警规则
     *
     * @return 启用的告警规则DTO列表
     */
    List<AlertRuleDTO> getEnabledAlertRules();
    
    /**
     * 获取指定服务的告警规则
     *
     * @param serviceName 服务名称
     * @return 告警规则DTO列表
     */
    List<AlertRuleDTO> getAlertRulesByService(String serviceName);
    
    /**
     * 添加告警规则条件
     *
     * @param ruleId 规则ID
     * @param conditionDTO 告警规则条件DTO
     * @return 更新后的告警规则DTO
     */
    AlertRuleDTO addRuleCondition(String ruleId, AlertRuleConditionDTO conditionDTO);
    
    /**
     * 确认告警
     *
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警DTO
     */
    AlertDTO acknowledgeAlert(String alertId, String operator, String note);
    
    /**
     * 解决告警
     *
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警DTO
     */
    AlertDTO resolveAlert(String alertId, String operator, String note);
    
    /**
     * 关闭告警
     *
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警DTO
     */
    AlertDTO closeAlert(String alertId, String operator, String note);
    
    /**
     * 抑制告警
     *
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警DTO
     */
    AlertDTO suppressAlert(String alertId, String operator, String note);
    
    /**
     * 获取告警
     *
     * @param alertId 告警ID
     * @return 告警DTO
     */
    AlertDTO getAlert(String alertId);
    
    /**
     * 获取所有告警
     *
     * @return 所有告警DTO列表
     */
    List<AlertDTO> getAllAlerts();
    
    /**
     * 获取活跃告警
     *
     * @return 活跃告警DTO列表
     */
    List<AlertDTO> getActiveAlerts();
    
    /**
     * 获取指定服务的告警
     *
     * @param serviceName 服务名称
     * @return 告警DTO列表
     */
    List<AlertDTO> getAlertsByService(String serviceName);
    
    /**
     * 获取指定服务实例的告警
     *
     * @param serviceInstanceId 服务实例ID
     * @return 告警DTO列表
     */
    List<AlertDTO> getAlertsByServiceInstance(String serviceInstanceId);
    
    /**
     * 获取指定时间范围内的告警
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 告警DTO列表
     */
    List<AlertDTO> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 分页获取告警
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortOrder 排序方向（asc/desc）
     * @return 告警DTO列表
     */
    List<AlertDTO> getAlertsPaged(int page, int size, String sortBy, String sortOrder);
    
    /**
     * 获取告警历史事件
     *
     * @param alertId 告警ID
     * @return 告警历史事件DTO列表
     */
    List<AlertEventDTO> getAlertHistory(String alertId);
    
    /**
     * 获取告警统计信息
     *
     * @return 告警统计信息
     */
    Map<String, Object> getAlertStatistics();
    
    /**
     * 获取告警趋势数据
     *
     * @param days 天数
     * @return 告警趋势数据（日期 -> 数量）
     */
    Map<String, Integer> getAlertTrend(int days);
    
    /**
     * 批量处理告警
     *
     * @param alertIds 告警ID列表
     * @param action 操作类型（acknowledge, resolve, close, suppress）
     * @param operator 操作人
     * @param note 备注信息
     * @return 成功处理的告警数量
     */
    int batchProcessAlerts(List<String> alertIds, String action, String operator, String note);
    
    /**
     * 测试告警规则
     *
     * @param alertRuleDTO 告警规则DTO
     * @param testValue 测试值
     * @return 是否会触发告警
     */
    boolean testAlertRule(AlertRuleDTO alertRuleDTO, double testValue);
    
    /**
     * 手动触发告警规则评估
     *
     * @param serviceInstanceId 服务实例ID
     * @return 触发的告警数量
     */
    int triggerAlertRuleEvaluation(String serviceInstanceId);
    
    /**
     * 按告警级别获取告警数量
     *
     * @return 告警级别统计（级别 -> 数量）
     */
    Map<String, Integer> getAlertCountBySeverity();
    
    /**
     * 按告警状态获取告警数量
     *
     * @return 告警状态统计（状态 -> 数量）
     */
    Map<String, Integer> getAlertCountByStatus();
    
    /**
     * 按服务获取告警数量
     *
     * @return 告警服务统计（服务名称 -> 数量）
     */
    Map<String, Integer> getAlertCountByService();
    
    /**
     * 获取告警处理时长统计
     *
     * @param days 天数
     * @return 处理时长统计（告警ID -> 处理时长（分钟））
     */
    Map<String, Long> getAlertResolutionTimeStatistics(int days);
    
    /**
     * 获取告警处理率
     *
     * @param days 天数
     * @return 告警处理率（0-1之间的小数）
     */
    double getAlertResolutionRate(int days);
}
