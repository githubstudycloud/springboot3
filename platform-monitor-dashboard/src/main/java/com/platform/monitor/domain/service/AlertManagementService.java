package com.platform.monitor.domain.service;

import com.platform.monitor.domain.model.Alert;
import com.platform.monitor.domain.model.AlertSeverity;
import com.platform.monitor.domain.model.AlertStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警管理领域服务接口
 */
public interface AlertManagementService {
    
    /**
     * 创建告警
     * 
     * @param alert 告警
     * @return 创建的告警
     */
    Alert createAlert(Alert alert);
    
    /**
     * 确认告警
     * 
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警
     */
    Alert acknowledgeAlert(String alertId, String operator, String note);
    
    /**
     * 解决告警
     * 
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警
     */
    Alert resolveAlert(String alertId, String operator, String note);
    
    /**
     * 关闭告警
     * 
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警
     */
    Alert closeAlert(String alertId, String operator, String note);
    
    /**
     * 抑制告警
     * 
     * @param alertId 告警ID
     * @param operator 操作人
     * @param note 备注信息
     * @return 更新后的告警
     */
    Alert suppressAlert(String alertId, String operator, String note);
    
    /**
     * 获取告警
     * 
     * @param alertId 告警ID
     * @return 告警
     */
    Alert getAlert(String alertId);
    
    /**
     * 获取所有告警
     * 
     * @return 所有告警列表
     */
    List<Alert> getAllAlerts();
    
    /**
     * 获取指定状态的告警
     * 
     * @param status 告警状态
     * @return 告警列表
     */
    List<Alert> getAlertsByStatus(AlertStatus status);
    
    /**
     * 获取指定级别的告警
     * 
     * @param severity 告警级别
     * @return 告警列表
     */
    List<Alert> getAlertsBySeverity(AlertSeverity severity);
    
    /**
     * 获取指定服务的告警
     * 
     * @param serviceName 服务名称
     * @return 告警列表
     */
    List<Alert> getAlertsByService(String serviceName);
    
    /**
     * 获取指定服务实例的告警
     * 
     * @param serviceInstanceId 服务实例ID
     * @return 告警列表
     */
    List<Alert> getAlertsByServiceInstance(String serviceInstanceId);
    
    /**
     * 获取指定时间范围内的告警
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 告警列表
     */
    List<Alert> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指定处理人的告警
     * 
     * @param handledBy 处理人
     * @return 告警列表
     */
    List<Alert> getAlertsByHandler(String handledBy);
    
    /**
     * 分页获取告警
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 告警列表
     */
    List<Alert> getAlertsPaged(int page, int size);
    
    /**
     * 删除告警
     * 
     * @param alertId 告警ID
     * @return 操作是否成功
     */
    boolean deleteAlert(String alertId);
    
    /**
     * 获取未解决的告警数量
     * 
     * @return 未解决的告警数量
     */
    long countActiveAlerts();
    
    /**
     * 获取按级别分组的告警统计
     * 
     * @return 告警统计结果（级别 -> 数量）
     */
    Map<AlertSeverity, Long> getAlertCountBySeverity();
    
    /**
     * 获取按状态分组的告警统计
     * 
     * @return 告警统计结果（状态 -> 数量）
     */
    Map<AlertStatus, Long> getAlertCountByStatus();
    
    /**
     * 获取按服务名称分组的告警统计
     * 
     * @return 告警统计结果（服务名称 -> 数量）
     */
    Map<String, Long> getAlertCountByService();
    
    /**
     * 自动处理过期告警
     * 
     * @param expireTimeMinutes 过期时间（分钟）
     * @return 处理的告警数量
     */
    int handleExpiredAlerts(int expireTimeMinutes);
    
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
     * 获取告警历史事件
     * 
     * @param alertId 告警ID
     * @return 告警历史事件列表
     */
    List<com.platform.monitor.domain.model.AlertEvent> getAlertHistory(String alertId);
}
