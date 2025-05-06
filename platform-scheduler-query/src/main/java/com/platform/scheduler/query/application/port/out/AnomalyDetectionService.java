package com.platform.scheduler.query.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 异常检测服务接口
 * 作为输出端口，定义异常检测服务的交互契约
 * 
 * @author platform
 */
public interface AnomalyDetectionService {

    /**
     * 检测异常任务
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param thresholdPercentage 阈值百分比
     * @return 异常任务列表
     */
    List<Map<String, Object>> detectAnomalousTasks(
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            double thresholdPercentage);
    
    /**
     * 分析任务执行时间异常
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 异常分析结果
     */
    Map<String, Object> analyzeExecutionTimeAnomaly(
            String jobId, 
            LocalDateTime startTime, 
            LocalDateTime endTime);
    
    /**
     * 分析失败率异常
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 异常分析结果
     */
    Map<String, Object> analyzeFailureRateAnomaly(
            String jobId, 
            LocalDateTime startTime, 
            LocalDateTime endTime);
    
    /**
     * 预测任务执行趋势
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param daysToPredict 预测天数
     * @return 预测结果
     */
    Map<String, Object> predictTaskExecutionTrend(
            String jobId, 
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            int daysToPredict);
}
