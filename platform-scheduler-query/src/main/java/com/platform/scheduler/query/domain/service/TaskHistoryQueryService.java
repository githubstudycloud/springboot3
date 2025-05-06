package com.platform.scheduler.query.domain.service;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import com.platform.scheduler.query.domain.model.task.TaskInstanceView;
import com.platform.scheduler.query.domain.model.task.TaskQueryCriteria;
import com.platform.scheduler.query.domain.model.task.TaskSearchResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务历史查询服务接口
 * 定义任务执行历史查询相关的领域服务
 * 
 * @author platform
 */
public interface TaskHistoryQueryService {

    /**
     * 根据时间范围查询历史任务
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param criteria 额外查询条件
     * @return 任务搜索结果
     */
    TaskSearchResult<TaskInstanceView> queryTaskHistory(LocalDateTime startTime, LocalDateTime endTime, TaskQueryCriteria criteria);
    
    /**
     * 获取任务执行历史趋势数据
     *
     * @param jobId 作业ID，如果为null则查询所有作业
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupByDay 是否按天分组，false则按小时分组
     * @return 趋势数据，键为时间点，值为任务数量
     */
    Map<String, Long> getTaskExecutionTrends(JobId jobId, LocalDate startDate, LocalDate endDate, boolean groupByDay);
    
    /**
     * 获取任务状态分布
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态分布图，键为状态名称，值为任务数量
     */
    Map<String, Long> getTaskStatusDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取作业执行历史记录
     *
     * @param jobId 作业ID
     * @param limit 最大记录数
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> getJobExecutionHistory(JobId jobId, int limit);
    
    /**
     * 获取任务执行时间分布
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param bucketSizeInSeconds 时间分布桶大小(秒)
     * @return 执行时间分布，键为时间范围，值为任务数量
     */
    Map<String, Long> getTaskDurationDistribution(LocalDateTime startTime, LocalDateTime endTime, int bucketSizeInSeconds);
    
    /**
     * 获取任务重试次数分布
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 重试次数分布，键为重试次数，值为任务数量
     */
    Map<Integer, Long> getTaskRetryDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务日志数据
     *
     * @param taskInstanceId 任务实例ID
     * @return 任务日志按时间顺序排列的列表
     */
    List<Map<String, Object>> getTaskLogs(TaskInstanceId taskInstanceId);
    
    /**
     * 导出任务历史数据
     *
     * @param criteria 查询条件
     * @param exportType 导出类型(CSV, EXCEL, PDF)
     * @return 导出文件数据
     */
    byte[] exportTaskHistory(TaskQueryCriteria criteria, String exportType);
    
    /**
     * 获取任务执行时间热力图数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 热力图数据，格式为[{date: '2023-01-01', hour: 0, count: 10}, ...]
     */
    List<Map<String, Object>> getTaskExecutionHeatmap(LocalDate startDate, LocalDate endDate);
    
    /**
     * 查询相关联的任务执行链
     *
     * @param taskInstanceId 任务实例ID
     * @return 关联的任务执行链，包含前置和后置任务
     */
    Map<String, List<TaskInstanceView>> getRelatedTaskChain(TaskInstanceId taskInstanceId);
    
    /**
     * 归档历史任务数据
     *
     * @param beforeDate 指定日期之前的数据将被归档
     * @return 归档的记录数
     */
    int archiveHistoricalTasks(LocalDate beforeDate);
}
