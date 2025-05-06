package com.platform.scheduler.query.application.port.out;

import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 执行器度量仓储接口
 * 作为输出端口，定义与执行器度量存储层的交互契约
 * 
 * @author platform
 */
public interface ExecutorMetricsRepository {

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
     * 获取指定时间段内所有执行器的统计指标
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 执行器统计指标映射，键为执行器ID，值为统计指标
     */
    Map<String, ExecutorMetrics> getExecutorMetricsInPeriod(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取当前所有执行器的统计指标
     *
     * @return 执行器统计指标列表
     */
    List<ExecutorMetrics> getAllCurrentExecutorMetrics();
    
    /**
     * 获取执行器度量排行榜
     *
     * @param sortField 排序字段
     * @param ascending 是否升序
     * @param limit 最大记录数
     * @return 执行器度量列表
     */
    List<ExecutorMetrics> getExecutorMetricsRanking(
            String sortField, 
            boolean ascending, 
            int limit);
}
