package com.platform.scheduler.query.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 告警服务接口
 * 定义任务告警和监控相关的领域服务
 * 
 * @author platform
 */
public interface AlertService {

    /**
     * 告警级别枚举
     */
    enum AlertLevel {
        /**
         * 信息级别
         */
        INFO,
        
        /**
         * 警告级别
         */
        WARNING,
        
        /**
         * 错误级别
         */
        ERROR,
        
        /**
         * 严重级别
         */
        CRITICAL
    }
    
    /**
     * 告警状态枚举
     */
    enum AlertStatus {
        /**
         * 活跃状态
         */
        ACTIVE,
        
        /**
         * 已确认状态
         */
        ACKNOWLEDGED,
        
        /**
         * 已解决状态
         */
        RESOLVED,
        
        /**
         * 已关闭状态
         */
        CLOSED
    }

    /**
     * 获取活跃告警列表
     *
     * @param limit 最大记录数
     * @return 告警信息列表
     */
    List<Map<String, Object>> getActiveAlerts(int limit);
    
    /**
     * 获取特定作业的告警列表
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 告警状态
     * @return 告警信息列表
     */
    List<Map<String, Object>> getJobAlerts(String jobId, LocalDateTime startTime, 
            LocalDateTime endTime, AlertStatus status);
    
    /**
     * 获取特定执行器的告警列表
     *
     * @param executorId 执行器ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 告警状态
     * @return 告警信息列表
     */
    List<Map<String, Object>> getExecutorAlerts(String executorId, LocalDateTime startTime, 
            LocalDateTime endTime, AlertStatus status);
    
    /**
     * 获取告警详情
     *
     * @param alertId 告警ID
     * @return 告警详情
     */
    Optional<Map<String, Object>> getAlertDetails(String alertId);
    
    /**
     * 确认告警
     *
     * @param alertId 告警ID
     * @param comment 确认备注
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean acknowledgeAlert(String alertId, String comment, String userId);
    
    /**
     * 解决告警
     *
     * @param alertId 告警ID
     * @param resolutionInfo 解决方案信息
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean resolveAlert(String alertId, String resolutionInfo, String userId);
    
    /**
     * 关闭告警
     *
     * @param alertId 告警ID
     * @param reason 关闭原因
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean closeAlert(String alertId, String reason, String userId);
    
    /**
     * 获取告警规则列表
     *
     * @return 告警规则列表
     */
    List<Map<String, Object>> getAlertRules();
    
    /**
     * 创建告警规则
     *
     * @param ruleDefinition 规则定义
     * @return 创建的规则ID
     */
    String createAlertRule(Map<String, Object> ruleDefinition);
    
    /**
     * 更新告警规则
     *
     * @param ruleId 规则ID
     * @param ruleDefinition 规则定义
     * @return 是否成功
     */
    boolean updateAlertRule(String ruleId, Map<String, Object> ruleDefinition);
    
    /**
     * 删除告警规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    boolean deleteAlertRule(String ruleId);
    
    /**
     * 启用告警规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    boolean enableAlertRule(String ruleId);
    
    /**
     * 禁用告警规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    boolean disableAlertRule(String ruleId);
    
    /**
     * 获取告警统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 告警统计信息
     */
    Map<String, Object> getAlertStatistics(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取未处理告警数量
     *
     * @return 未处理告警数量
     */
    long getUnhandledAlertCount();
    
    /**
     * 测试告警规则
     *
     * @param ruleDefinition 规则定义
     * @param testData 测试数据
     * @return 测试结果
     */
    Map<String, Object> testAlertRule(Map<String, Object> ruleDefinition, Map<String, Object> testData);
}
