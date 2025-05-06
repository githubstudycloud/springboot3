package com.platform.scheduler.query.application.port.out;

import com.platform.scheduler.query.domain.model.metrics.TaskMetrics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务分析仓储接口
 * 作为输出端口，定义与存储层的交互契约
 * 
 * @author platform
 */
public interface TaskAnalyticsRepository {

    /**
     * 获取任务统计指标
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务统计指标
     */
    TaskMetrics getTaskMetrics(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务状态分布
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态分布，键为状态名称，值为任务数量
     */
    Map<String, Long> getTaskStatusDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务执行时间列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 执行时间列表(毫秒)
     */
    List<Long> getTaskDurations(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务重试分布
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 重试分布，键为重试次数，值为任务数量
     */
    Map<Integer, Long> getTaskRetryDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取按天统计的任务成功率
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功率数据列表，每项包含日期和成功率
     */
    List<Map<String, Object>> getTaskSuccessRateByDay(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取每日任务执行峰值时间
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 峰值时间，键为日期，值为小时(0-23)
     */
    Map<LocalDate, Integer> getTaskExecutionPeakHours(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取作业执行时间趋势
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势数据列表，每项包含日期和平均执行时间
     */
    List<Map<String, Object>> getJobExecutionTimeTrend(String jobId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务执行热力图数据
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 热力图数据，格式为[{date: '2023-01-01', hour: 0, count: 10}, ...]
     */
    List<Map<String, Object>> getTaskExecutionHeatmap(LocalDateTime startTime, LocalDateTime endTime);
}
