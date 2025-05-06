package com.platform.scheduler.query.application.service;

import com.platform.scheduler.domain.model.job.JobId;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 趋势分析服务
 * 提供各种任务执行趋势分析的方法
 * 
 * @author platform
 */
@Slf4j
public class TrendAnalysisService {

    private TrendAnalysisService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 获取指定时间范围内的任务成功率趋势
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param taskAnalyticsRepository 任务分析仓储
     * @return 任务成功率趋势数据
     */
    public static Map<LocalDate, Double> getTaskSuccessRateTrend(
            LocalDate startDate, 
            LocalDate endDate,
            com.platform.scheduler.query.application.port.out.TaskAnalyticsRepository taskAnalyticsRepository) {
        
        log.debug("获取任务成功率趋势: {} - {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            log.warn("获取任务成功率趋势时日期范围参数无效");
            return Collections.emptyMap();
        }
        
        try {
            // 查询原始数据
            List<Map<String, Object>> rawData = taskAnalyticsRepository.getTaskSuccessRateByDay(
                    startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay().minusNanos(1));
            
            // 转换为所需格式
            Map<LocalDate, Double> trend = new HashMap<>();
            for (Map<String, Object> data : rawData) {
                LocalDate date = (LocalDate) data.get("date");
                Double successRate = (Double) data.get("successRate");
                trend.put(date, successRate);
            }
            
            // 填充缺失的日期
            return DataUtils.fillMissingDates(trend, startDate, endDate);
        } catch (Exception e) {
            log.error("获取任务成功率趋势发生异常", e);
            return Collections.emptyMap();
        }
    }
    
    /**
     * 获取每日任务执行峰值时间
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param taskAnalyticsRepository 任务分析仓储
     * @return 每日任务执行峰值时间
     */
    public static Map<LocalDate, Integer> getTaskExecutionPeakHours(
            LocalDate startDate, 
            LocalDate endDate,
            com.platform.scheduler.query.application.port.out.TaskAnalyticsRepository taskAnalyticsRepository) {
        
        log.debug("获取每日任务执行峰值时间: {} - {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            log.warn("获取每日任务执行峰值时间时日期范围参数无效");
            return Collections.emptyMap();
        }
        
        try {
            return taskAnalyticsRepository.getTaskExecutionPeakHours(
                    startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay().minusNanos(1));
        } catch (Exception e) {
            log.error("获取每日任务执行峰值时间发生异常", e);
            return Collections.emptyMap();
        }
    }
    
    /**
     * 获取作业执行时间趋势
     *
     * @param jobId 作业ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param taskAnalyticsRepository 任务分析仓储
     * @return 作业执行时间趋势
     */
    public static Map<LocalDate, Double> getJobExecutionTimeTrend(
            JobId jobId, 
            LocalDate startDate, 
            LocalDate endDate,
            com.platform.scheduler.query.application.port.out.TaskAnalyticsRepository taskAnalyticsRepository) {
        
        log.debug("获取作业执行时间趋势: {}, {} - {}", jobId, startDate, endDate);
        
        if (jobId == null || startDate == null || endDate == null) {
            log.warn("获取作业执行时间趋势时参数无效");
            return Collections.emptyMap();
        }
        
        try {
            // 查询原始数据
            List<Map<String, Object>> rawData = taskAnalyticsRepository.getJobExecutionTimeTrend(
                    jobId.getValue(), 
                    startDate.atStartOfDay(), 
                    endDate.plusDays(1).atStartOfDay().minusNanos(1));
            
            // 转换为所需格式
            Map<LocalDate, Double> trend = new HashMap<>();
            for (Map<String, Object> data : rawData) {
                LocalDate date = (LocalDate) data.get("date");
                Double avgExecutionTime = (Double) data.get("avgExecutionTime");
                trend.put(date, avgExecutionTime);
            }
            
            // 填充缺失的日期
            return DataUtils.fillMissingDates(trend, startDate, endDate);
        } catch (Exception e) {
            log.error("获取作业执行时间趋势发生异常: {}", jobId, e);
            return Collections.emptyMap();
        }
    }
}
