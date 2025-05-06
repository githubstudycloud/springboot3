package com.platform.scheduler.query.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 日志查询服务接口
 * 定义任务执行日志查询相关的领域服务
 * 
 * @author platform
 */
public interface LogQueryService {

    /**
     * 日志级别枚举
     */
    enum LogLevel {
        /**
         * 跟踪级别
         */
        TRACE,
        
        /**
         * 调试级别
         */
        DEBUG,
        
        /**
         * 信息级别
         */
        INFO,
        
        /**
         * 警告级别
         */
        WARN,
        
        /**
         * 错误级别
         */
        ERROR
    }

    /**
     * 根据任务ID获取执行日志
     *
     * @param taskInstanceId 任务实例ID
     * @return 日志条目列表
     */
    List<Map<String, Object>> getTaskLogs(String taskInstanceId);
    
    /**
     * 根据任务ID和日志级别获取执行日志
     *
     * @param taskInstanceId 任务实例ID
     * @param level 日志级别
     * @return 日志条目列表
     */
    List<Map<String, Object>> getTaskLogsByLevel(String taskInstanceId, LogLevel level);
    
    /**
     * 搜索任务日志
     *
     * @param taskInstanceId 任务实例ID
     * @param keyword 关键字
     * @return 日志条目列表
     */
    List<Map<String, Object>> searchTaskLogs(String taskInstanceId, String keyword);
    
    /**
     * 获取最近的错误日志
     *
     * @param limit 最大记录数
     * @return 日志条目列表
     */
    List<Map<String, Object>> getRecentErrorLogs(int limit);
    
    /**
     * 获取任务日志统计信息
     *
     * @param taskInstanceId 任务实例ID
     * @return 日志统计信息
     */
    Map<String, Long> getTaskLogStats(String taskInstanceId);
    
    /**
     * 根据时间范围获取系统级日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param minLevel 最低日志级别
     * @param limit 最大记录数
     * @return 日志条目列表
     */
    List<Map<String, Object>> getSystemLogs(LocalDateTime startTime, LocalDateTime endTime, 
            LogLevel minLevel, int limit);
    
    /**
     * 获取特定作业的所有相关日志
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @return 日志条目列表
     */
    List<Map<String, Object>> getJobRelatedLogs(String jobId, LocalDateTime startTime, 
            LocalDateTime endTime, int limit);
    
    /**
     * 获取特定执行器的日志
     *
     * @param executorId 执行器ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @return 日志条目列表
     */
    List<Map<String, Object>> getExecutorLogs(String executorId, LocalDateTime startTime, 
            LocalDateTime endTime, int limit);
    
    /**
     * 分析日志中的错误模式
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误模式分析结果
     */
    List<Map<String, Object>> analyzeErrorPatterns(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取日志级别分布
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志级别分布统计
     */
    Map<LogLevel, Long> getLogLevelDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务执行阶段日志
     *
     * @param taskInstanceId 任务实例ID
     * @return 执行阶段日志
     */
    List<Map<String, Object>> getTaskExecutionStageLogs(String taskInstanceId);
    
    /**
     * 根据关键字在所有日志中搜索
     *
     * @param keyword 关键字
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @return 日志条目列表
     */
    List<Map<String, Object>> searchAllLogs(String keyword, LocalDateTime startTime, 
            LocalDateTime endTime, int limit);
    
    /**
     * 获取特定日志详情
     *
     * @param logId 日志ID
     * @return 日志详情
     */
    Map<String, Object> getLogDetails(String logId);
    
    /**
     * 获取未读错误日志计数
     *
     * @return 未读错误日志计数
     */
    long getUnreadErrorLogsCount();
    
    /**
     * 标记日志为已读
     *
     * @param logIds 日志ID列表
     * @return 操作成功数量
     */
    int markLogsAsRead(List<String> logIds);
}
