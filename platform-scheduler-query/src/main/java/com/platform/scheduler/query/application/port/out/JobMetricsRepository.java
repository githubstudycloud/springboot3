package com.platform.scheduler.query.application.port.out;

import com.platform.scheduler.query.domain.model.metrics.JobMetrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 作业度量仓储接口
 * 作为输出端口，定义与作业度量存储层的交互契约
 * 
 * @author platform
 */
public interface JobMetricsRepository {

    /**
     * 获取特定作业的统计指标
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 作业统计指标
     */
    Optional<JobMetrics> getJobMetrics(String jobId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指定时间段内所有作业的统计指标
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 作业统计指标映射，键为作业ID，值为统计指标
     */
    Map<String, JobMetrics> getJobMetricsInPeriod(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取作业度量排行榜
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param sortField 排序字段
     * @param ascending 是否升序
     * @param limit 最大记录数
     * @return 作业度量列表
     */
    List<JobMetrics> getJobMetricsRanking(
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            String sortField, 
            boolean ascending, 
            int limit);
}
