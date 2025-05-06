package com.platform.scheduler.query.application.service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 异常检测助手
 * 提供异常任务检测和分析功能
 * 
 * @author platform
 */
@Slf4j
public class AnomalyDetectionHelper {

    private AnomalyDetectionHelper() {
        // 私有构造方法，防止实例化
    }

    /**
     * 分析并预测异常任务
     *
     * @param thresholdPercentage 阈值百分比
     * @param anomalyDetectionService 异常检测服务
     * @return 异常任务列表
     */
    public static List<Map<String, Object>> detectAnomalousTasks(
            double thresholdPercentage,
            com.platform.scheduler.query.application.port.out.AnomalyDetectionService anomalyDetectionService) {
        
        log.debug("分析并预测异常任务, thresholdPercentage={}", thresholdPercentage);
        
        if (thresholdPercentage <= 0 || thresholdPercentage > 100) {
            thresholdPercentage = 20.0; // 默认阈值
        }
        
        try {
            // 获取历史执行数据
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusDays(30); // 分析最近30天的数据
            
            // 使用异常检测服务进行检测
            return anomalyDetectionService.detectAnomalousTasks(startTime, endTime, thresholdPercentage);
        } catch (Exception e) {
            log.error("分析并预测异常任务发生异常", e);
            return Collections.emptyList();
        }
    }
}
