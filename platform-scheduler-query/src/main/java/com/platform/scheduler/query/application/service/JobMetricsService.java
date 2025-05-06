package com.platform.scheduler.query.application.service;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.query.domain.model.metrics.JobMetrics;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 作业指标服务
 * 提供计算和获取作业统计指标的方法
 * 
 * @author platform
 */
@Slf4j
public class JobMetricsService {

    private JobMetricsService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 获取特定作业的统计指标
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param jobMetricsRepository 作业度量仓储
     * @return 作业统计指标
     */
    public static Optional<JobMetrics> getJobMetrics(
            JobId jobId, 
            LocalDateTime startTime, 
            LocalDateTime endTime,
            com.platform.scheduler.query.application.port.out.JobMetricsRepository jobMetricsRepository) {
        
        log.debug("获取作业统计指标: {}, {} - {}", jobId, startTime, endTime);
        
        if (jobId == null || startTime == null || endTime == null) {
            log.warn("获取作业统计指标时参数无效");
            return Optional.empty();
        }
        
        try {
            return jobMetricsRepository.getJobMetrics(jobId.getValue(), startTime, endTime);
        } catch (Exception e) {
            log.error("获取作业统计指标发生异常: {}", jobId, e);
            return Optional.empty();
        }
    }
}
