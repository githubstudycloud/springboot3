package com.platform.scheduler.query.application.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 数据处理工具类
 * 提供数据处理和转换的通用方法
 * 
 * @author platform
 */
public class DataUtils {

    private DataUtils() {
        // 私有构造方法，防止实例化
    }
    
    /**
     * 计算百分比变化
     * 
     * @param newValue 新值
     * @param oldValue 旧值
     * @return 百分比变化
     */
    public static double calculatePercentageChange(double newValue, double oldValue) {
        if (oldValue == 0) {
            return newValue > 0 ? 100.0 : 0.0;
        }
        return ((newValue - oldValue) / Math.abs(oldValue)) * 100.0;
    }
    
    /**
     * 计算百分比变化
     * 
     * @param newValue 新值
     * @param oldValue 旧值
     * @return 百分比变化
     */
    public static double calculatePercentageChange(long newValue, long oldValue) {
        if (oldValue == 0) {
            return newValue > 0 ? 100.0 : 0.0;
        }
        return ((double) (newValue - oldValue) / Math.abs(oldValue)) * 100.0;
    }
    
    /**
     * 填充缺失的日期数据
     *
     * @param data 原始数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param <T> 数据类型
     * @return 填充后的数据
     */
    public static <T> Map<LocalDate, T> fillMissingDates(Map<LocalDate, T> data, LocalDate startDate, LocalDate endDate) {
        SortedMap<LocalDate, T> result = new TreeMap<>();
        
        // 获取默认值（如果数据集非空）
        T defaultValue = data != null && !data.isEmpty() ? 
                data.values().iterator().next() instanceof Number ? 
                        (T) Double.valueOf(0.0) : null : null;
        
        // 遍历日期范围，填充缺失的日期
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            result.put(current, data != null && data.containsKey(current) ? 
                    data.get(current) : defaultValue);
            current = current.plusDays(1);
        }
        
        return result;
    }
    
    /**
     * 构建执行时间分布
     *
     * @param durations 执行时间列表
     * @param bucketSizeInSeconds 桶大小(秒)
     * @return 执行时间分布
     */
    public static Map<String, Long> buildDurationDistribution(java.util.List<Long> durations, int bucketSizeInSeconds) {
        Map<String, Long> distribution = new TreeMap<>();
        
        if (durations == null || durations.isEmpty()) {
            return distribution;
        }
        
        // 计算最大执行时间，向上取整到最近的桶大小的倍数
        long maxDuration = durations.stream().max(Long::compare).orElse(0L);
        long maxDurationInSeconds = (maxDuration / 1000) + 1;
        int bucketCount = (int) Math.ceil((double) maxDurationInSeconds / bucketSizeInSeconds);
        
        // 初始化所有桶
        for (int i = 0; i < bucketCount; i++) {
            int lowerBound = i * bucketSizeInSeconds;
            int upperBound = (i + 1) * bucketSizeInSeconds;
            distribution.put(lowerBound + "-" + upperBound + "s", 0L);
        }
        
        // 统计每个桶的任务数量
        for (Long duration : durations) {
            if (duration == null) continue;
            
            int bucketIndex = (int) ((duration / 1000) / bucketSizeInSeconds);
            if (bucketIndex >= bucketCount) {
                bucketIndex = bucketCount - 1; // 最大桶
            }
            
            int lowerBound = bucketIndex * bucketSizeInSeconds;
            int upperBound = (bucketIndex + 1) * bucketSizeInSeconds;
            String bucketKey = lowerBound + "-" + upperBound + "s";
            
            distribution.put(bucketKey, distribution.getOrDefault(bucketKey, 0L) + 1);
        }
        
        return distribution;
    }
}
