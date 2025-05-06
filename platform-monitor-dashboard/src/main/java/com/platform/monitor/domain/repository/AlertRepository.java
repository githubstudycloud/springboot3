package com.platform.monitor.domain.repository;

import com.platform.monitor.domain.model.Alert;
import com.platform.monitor.domain.model.AlertSeverity;
import com.platform.monitor.domain.model.AlertStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 告警仓储接口
 */
public interface AlertRepository {
    
    /**
     * 保存告警
     *
     * @param alert 告警
     * @return 保存后的告警
     */
    Alert save(Alert alert);
    
    /**
     * 根据ID查找告警
     *
     * @param id 告警ID
     * @return 告警可选结果
     */
    Optional<Alert> findById(String id);
    
    /**
     * 根据告警状态查找告警
     *
     * @param status 告警状态
     * @return 告警列表
     */
    List<Alert> findByStatus(AlertStatus status);
    
    /**
     * 根据告警级别查找告警
     *
     * @param severity 告警级别
     * @return 告警列表
     */
    List<Alert> findBySeverity(AlertSeverity severity);
    
    /**
     * 根据服务实例ID查找告警
     *
     * @param serviceInstanceId 服务实例ID
     * @return 告警列表
     */
    List<Alert> findByServiceInstanceId(String serviceInstanceId);
    
    /**
     * 根据服务名称查找告警
     *
     * @param serviceName 服务名称
     * @return 告警列表
     */
    List<Alert> findByServiceName(String serviceName);
    
    /**
     * 根据告警规则ID查找告警
     *
     * @param ruleId 告警规则ID
     * @return 告警列表
     */
    List<Alert> findByRuleId(String ruleId);
    
    /**
     * 查找指定时间范围内的告警
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 告警列表
     */
    List<Alert> findByFirstOccurTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取所有告警
     *
     * @return 所有告警列表
     */
    List<Alert> findAll();
    
    /**
     * 分页查询告警
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 告警列表
     */
    List<Alert> findAllPaged(int page, int size);
    
    /**
     * 删除告警
     *
     * @param id 告警ID
     */
    void deleteById(String id);
    
    /**
     * 获取未解决的告警数量
     *
     * @return 未解决的告警数量
     */
    long countActiveAlerts();
    
    /**
     * 获取指定服务未解决的告警数量
     *
     * @param serviceName 服务名称
     * @return 未解决的告警数量
     */
    long countActiveAlertsByServiceName(String serviceName);
    
    /**
     * 查找需要自动关闭的告警
     *
     * @param status 告警状态
     * @param beforeTime 截止时间
     * @return 告警列表
     */
    List<Alert> findForAutoClose(AlertStatus status, LocalDateTime beforeTime);
    
    /**
     * 查找未解决的指定规则的告警
     *
     * @param ruleId 告警规则ID
     * @param serviceInstanceId 服务实例ID
     * @return 告警列表
     */
    List<Alert> findActiveByRuleIdAndServiceInstanceId(String ruleId, String serviceInstanceId);
    
    /**
     * 根据处理人查找告警
     *
     * @param handledBy 处理人
     * @return 告警列表
     */
    List<Alert> findByHandledBy(String handledBy);
    
    /**
     * 按照不同维度统计告警数量
     *
     * @param dimension 统计维度（"severity", "status", "serviceName"）
     * @return 统计结果（维度值 -> 数量）
     */
    java.util.Map<String, Long> countByDimension(String dimension);
}
