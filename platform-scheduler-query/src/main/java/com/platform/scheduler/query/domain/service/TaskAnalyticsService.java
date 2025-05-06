package com.platform.scheduler.query.domain.service;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;
import com.platform.scheduler.query.domain.model.metrics.JobMetrics;
import com.platform.scheduler.query.domain.model.metrics.TaskMetrics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 任务统计分析服务接口
 * 定义任务执行统计和分析相关的领域服务
 * 
 * @author platform
 */
public interface TaskAnalyticsService {

    /**
     * 获取时间范围内的任务统计指标
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务统计指标
     */
    TaskMetrics getTaskMetrics(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取特定作业的统计指标
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 作业统计指标
     */
    Optional<JobMetrics> getJobMetrics(JobId jobId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取特定执行器的统计指标
     *
     * @param executorId 执行器ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 执行器统计指标
     */
    Optional<ExecutorMetrics> getExecutorMetrics(String executorId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的任务成功率趋势
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 成功率趋势，键为日期，值为成功率(0-100)
     */
    Map<LocalDate, Double> getTaskSuccessRateTrend(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取任务性能排行榜
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @param ascending 是否按升序排序(true表示最快的在前)
     * @return 作业统计指标列表
     */
    List<JobMetrics> getTaskPerformanceRanking(LocalDateTime startTime, LocalDateTime endTime, 
            int limit, boolean ascending);
    
    /**
     * 获取任务失败率排行榜
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @return 作业统计指标列表，按失败率降序排序
     */
    List<JobMetrics> getTaskFailureRateRanking(LocalDateTime startTime, LocalDateTime endTime, int limit);
    
    /**
     * 获取执行器负载排行榜
     *
     * @param limit 最大记录数
     * @return 执行器统计指标列表，按负载降序排序
     */
    List<ExecutorMetrics> getExecutorLoadRanking(int limit);
    
    /**
     * 计算系统整体健康分数
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 健康分数(0-100)
     */
    double calculateSystemHealthScore(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取每日任务执行峰值时间
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 峰值时间分布，键为日期，值为小时(0-23)
     */
    Map<LocalDate, Integer> getTaskExecutionPeakHours(LocalDate startDate, LocalDate endDate);
    
    /**
     * 分析并预测异常任务
     *
     * @param thresholdPercentage 阈值百分比
     * @return 潜在异常任务列表
     */
    List<Map<String, Object>> detectAnomalousTasks(double thresholdPercentage);
    
    /**
     * 获取作业执行时间趋势
     *
     * @param jobId 作业ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 执行时间趋势，键为日期，值为平均执行时间(毫秒)
     */
    Map<LocalDate, Double> getJobExecutionTimeTrend(JobId jobId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 生成任务执行报表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reportType 报表类型(SUMMARY, DETAILED, EXECUTIVE)
     * @return 报表数据
     */
    Map<String, Object> generateTaskExecutionReport(LocalDateTime startTime, LocalDateTime endTime, 
            String reportType);
    
    /**
     * 获取资源利用率统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 资源利用率数据
     */
    Map<String, Object> getResourceUtilizationStats(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取任务执行对比数据
     *
     * @param period1Start 第一个周期开始时间
     * @param period1End 第一个周期结束时间
     * @param period2Start 第二个周期开始时间
     * @param period2End 第二个周期结束时间
     * @return 对比数据
     */
    Map<String, Object> compareTaskExecutionPeriods(
            LocalDateTime period1Start, LocalDateTime period1End,
            LocalDateTime period2Start, LocalDateTime period2End);
}
