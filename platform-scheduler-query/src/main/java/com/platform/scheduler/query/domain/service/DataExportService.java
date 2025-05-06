package com.platform.scheduler.query.domain.service;

import com.platform.scheduler.query.domain.model.task.TaskQueryCriteria;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 数据导出服务接口
 * 定义数据导出相关的领域服务
 * 
 * @author platform
 */
public interface DataExportService {

    /**
     * 导出格式枚举
     */
    enum ExportFormat {
        /**
         * CSV格式
         */
        CSV,
        
        /**
         * Excel格式
         */
        EXCEL,
        
        /**
         * PDF格式
         */
        PDF,
        
        /**
         * JSON格式
         */
        JSON
    }

    /**
     * 导出任务执行历史数据
     *
     * @param criteria 查询条件
     * @param format 导出格式
     * @return 导出文件数据
     */
    byte[] exportTaskHistory(TaskQueryCriteria criteria, ExportFormat format);
    
    /**
     * 导出任务统计分析报表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式
     * @return 导出文件数据
     */
    byte[] exportTaskAnalytics(LocalDateTime startTime, LocalDateTime endTime, ExportFormat format);
    
    /**
     * 导出作业执行报表
     *
     * @param jobIds 作业ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式
     * @return 导出文件数据
     */
    byte[] exportJobReport(java.util.List<String> jobIds, LocalDateTime startTime, LocalDateTime endTime, ExportFormat format);
    
    /**
     * 导出执行器性能报表
     *
     * @param executorIds 执行器ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式
     * @return 导出文件数据
     */
    byte[] exportExecutorPerformance(java.util.List<String> executorIds, LocalDateTime startTime, LocalDateTime endTime, ExportFormat format);
    
    /**
     * 导出任务执行日志
     *
     * @param taskInstanceId 任务实例ID
     * @param format 导出格式
     * @return 导出文件数据
     */
    byte[] exportTaskLogs(String taskInstanceId, ExportFormat format);
    
    /**
     * 导出系统运行状态摘要
     *
     * @param includeMetrics 是否包含指标数据
     * @param includeTrends 是否包含趋势数据
     * @param format 导出格式
     * @return 导出文件数据
     */
    byte[] exportSystemStatusSummary(boolean includeMetrics, boolean includeTrends, ExportFormat format);
    
    /**
     * 异步导出任务历史(适用于大数据量)
     *
     * @param criteria 查询条件
     * @param format 导出格式
     * @param callbackUrl 完成回调地址
     * @return 任务ID
     */
    String exportTaskHistoryAsync(TaskQueryCriteria criteria, ExportFormat format, String callbackUrl);
    
    /**
     * 获取异步导出任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态和结果
     */
    Map<String, Object> getExportTaskStatus(String taskId);
}
