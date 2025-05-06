package com.platform.scheduler.query.application.service;

import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;
import com.platform.scheduler.query.domain.model.metrics.JobMetrics;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排名服务
 * 提供各种排行榜和排序功能
 * 
 * @author platform
 */
@Slf4j
public class RankingService {

    private RankingService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 获取任务性能排行榜
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @param ascending 是否按升序排序
     * @param jobMetricsRepository 作业度量仓储
     * @return 任务性能排行榜
     */
    public static List<JobMetrics> getTaskPerformanceRanking(
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            int limit, 
            boolean ascending,
            com.platform.scheduler.query.application.port.out.JobMetricsRepository jobMetricsRepository) {
        
        log.debug("获取任务性能排行榜: {} - {}, limit={}, ascending={}", startTime, endTime, limit, ascending);
        
        if (startTime == null || endTime == null) {
            log.warn("获取任务性能排行榜时时间范围参数无效");
            return Collections.emptyList();
        }
        
        if (limit <= 0) {
            limit = 10; // 默认限制
        }
        
        try {
            return jobMetricsRepository.getJobMetricsRanking(startTime, endTime, "averageExecutionTime", ascending, limit);
        } catch (Exception e) {
            log.error("获取任务性能排行榜发生异常", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 获取任务失败率排行榜
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @param jobMetricsRepository 作业度量仓储
     * @return 任务失败率排行榜
     */
    public static List<JobMetrics> getTaskFailureRateRanking(
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            int limit,
            com.platform.scheduler.query.application.port.out.JobMetricsRepository jobMetricsRepository) {
        
        log.debug("获取任务失败率排行榜: {} - {}, limit={}", startTime, endTime, limit);
        
        if (startTime == null || endTime == null) {
            log.warn("获取任务失败率排行榜时时间范围参数无效");
            return Collections.emptyList();
        }
        
        if (limit <= 0) {
            limit = 10; // 默认限制
        }
        
        try {
            // 获取所有作业的统计数据
            Map<String, JobMetrics> allJobMetrics = jobMetricsRepository.getJobMetricsInPeriod(startTime, endTime);
            
            // 计算失败率并排序
            return allJobMetrics.values().stream()
                    .filter(metrics -> metrics.getTotalExecutions() > 0) // 过滤掉没有执行的作业
                    .sorted(Comparator.comparingDouble(JobMetrics::getFailureRate).reversed()) // 按失败率降序排序
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取任务失败率排行榜发生异常", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 获取执行器负载排行榜
     *
     * @param limit 最大记录数
     * @param executorMetricsRepository 执行器度量仓储
     * @return 执行器负载排行榜
     */
    public static List<ExecutorMetrics> getExecutorLoadRanking(
            int limit,
            com.platform.scheduler.query.application.port.out.ExecutorMetricsRepository executorMetricsRepository) {
        
        log.debug("获取执行器负载排行榜, limit={}", limit);
        
        if (limit <= 0) {
            limit = 10; // 默认限制
        }
        
        try {
            // 获取当前所有执行器的度量数据
            List<ExecutorMetrics> executorMetrics = executorMetricsRepository.getAllCurrentExecutorMetrics();
            
            // 按负载（当前负载/最大并发数）排序
            return executorMetrics.stream()
                    .filter(metrics -> metrics.getMaxConcurrency() > 0) // 过滤掉无效的执行器
                    .sorted(Comparator.comparingDouble(e -> 
                        (double) e.getCurrentLoad() / e.getMaxConcurrency()).reversed()) // 按负载率降序排序
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取执行器负载排行榜发生异常", e);
            return Collections.emptyList();
        }
    }
}
