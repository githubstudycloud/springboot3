package com.platform.scheduler.query.application.service;

import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 执行器指标服务
 * 提供计算和获取执行器统计指标的方法
 * 
 * @author platform
 */
@Slf4j
public class ExecutorMetricsService {

    private ExecutorMetricsService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 获取特定执行器的统计指标
     *
     * @param executorId 执行器ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param executorMetricsRepository 执行器度量仓储
     * @return 执行器统计指标
     */
    public static Optional<ExecutorMetrics> getExecutorMetrics(
            String executorId, 
            LocalDateTime startTime, 
            LocalDateTime endTime,
            com.platform.scheduler.query.application.port.out.ExecutorMetricsRepository executorMetricsRepository) {
        
        log.debug("获取执行器统计指标: {}, {} - {}", executorId, startTime, endTime);
        
        if (executorId == null || executorId.trim().isEmpty() || startTime == null || endTime == null) {
            log.warn("获取执行器统计指标时参数无效");
            return Optional.empty();
        }
        
        try {
            return executorMetricsRepository.getExecutorMetrics(executorId, startTime, endTime);
        } catch (Exception e) {
            log.error("获取执行器统计指标发生异常: {}", executorId, e);
            return Optional.empty();
        }
    }
}
