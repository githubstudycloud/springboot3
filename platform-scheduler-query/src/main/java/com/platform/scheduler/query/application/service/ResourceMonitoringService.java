package com.platform.scheduler.query.application.service;

import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资源监控服务
 * 提供资源利用率监控和统计功能
 * 
 * @author platform
 */
@Slf4j
public class ResourceMonitoringService {

    private ResourceMonitoringService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 获取资源利用率统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param executorMetricsRepository 执行器度量仓储
     * @return 资源利用率统计
     */
    public static Map<String, Object> getResourceUtilizationStats(
            LocalDateTime startTime, 
            LocalDateTime endTime,
            com.platform.scheduler.query.application.port.out.ExecutorMetricsRepository executorMetricsRepository) {
        
        log.debug("获取资源利用率统计: {} - {}", startTime, endTime);
        
        if (startTime == null || endTime == null) {
            log.warn("获取资源利用率统计时时间范围参数无效");
            return Collections.emptyMap();
        }
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取当前执行器列表
            List<ExecutorMetrics> executorMetricsList = executorMetricsRepository.getAllCurrentExecutorMetrics();
            
            // 计算CPU使用率统计
            DoubleSummaryStatistics cpuStats = executorMetricsList.stream()
                    .mapToDouble(ExecutorMetrics::getCpuUsage)
                    .summaryStatistics();
            
            Map<String, Object> cpuUsage = new HashMap<>();
            cpuUsage.put("average", cpuStats.getAverage());
            cpuUsage.put("max", cpuStats.getMax());
            cpuUsage.put("min", cpuStats.getMin());
            stats.put("cpuUsage", cpuUsage);
            
            // 计算内存使用率统计
            DoubleSummaryStatistics memoryStats = executorMetricsList.stream()
                    .mapToDouble(ExecutorMetrics::getMemoryUsage)
                    .summaryStatistics();
            
            Map<String, Object> memoryUsage = new HashMap<>();
            memoryUsage.put("average", memoryStats.getAverage());
            memoryUsage.put("max", memoryStats.getMax());
            memoryUsage.put("min", memoryStats.getMin());
            stats.put("memoryUsage", memoryUsage);
            
            // 计算并发使用率统计
            List<Double> concurrencyRates = executorMetricsList.stream()
                    .filter(m -> m.getMaxConcurrency() > 0)
                    .map(m -> (double) m.getCurrentLoad() / m.getMaxConcurrency() * 100.0)
                    .collect(Collectors.toList());
            
            DoubleSummaryStatistics concurrencyStats = concurrencyRates.stream()
                    .mapToDouble(Double::doubleValue)
                    .summaryStatistics();
            
            Map<String, Object> concurrencyUsage = new HashMap<>();
            concurrencyUsage.put("average", concurrencyStats.getAverage());
            concurrencyUsage.put("max", concurrencyStats.getMax());
            concurrencyUsage.put("min", concurrencyStats.getMin());
            stats.put("concurrencyUsage", concurrencyUsage);
            
            // 添加执行器数量信息
            stats.put("totalExecutors", executorMetricsList.size());
            stats.put("activeExecutors", executorMetricsList.stream()
                    .filter(m -> m.getCurrentLoad() > 0)
                    .count());
            
            return stats;
        } catch (Exception e) {
            log.error("获取资源利用率统计发生异常", e);
            return Collections.emptyMap();
        }
    }
}
