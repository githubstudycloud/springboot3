package com.platform.sqlanalyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 历史数据清理服务
 * 定期清理过期的历史性能数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryDataCleanupService {
    
    @Value("${sql-analyzer.history-data-path:./data/history}")
    private String historyDataPath;
    
    @Value("${sql-analyzer.data-collection.retention-days.hourly:7}")
    private int hourlyRetentionDays;
    
    @Value("${sql-analyzer.data-collection.retention-days.daily:90}")
    private int dailyRetentionDays;
    
    @Value("${sql-analyzer.data-collection.retention-days.weekly:365}")
    private int weeklyRetentionDays;
    
    @Value("${sql-analyzer.data-collection.enabled:false}")
    private boolean dataCollectionEnabled;
    
    /**
     * 执行历史数据清理
     * 默认每天凌晨2点执行
     */
    @Scheduled(cron = "${sql-analyzer.data-collection.cleanup-cron:0 0 2 * * ?}")
    public void cleanupHistoryData() {
        if (!dataCollectionEnabled) {
            return;
        }
        
        log.info("开始清理历史性能数据...");
        
        try {
            // 创建保留期限映射
            Map<String, Integer> retentionMap = new HashMap<>();
            retentionMap.put("hourly", hourlyRetentionDays);
            retentionMap.put("daily", dailyRetentionDays);
            retentionMap.put("weekly", weeklyRetentionDays);
            retentionMap.put("manual", 30); // 手动收集的数据保留30天
            
            // 获取历史数据目录
            File historyDir = new File(historyDataPath);
            if (!historyDir.exists() || !historyDir.isDirectory()) {
                log.info("历史数据目录不存在: {}", historyDataPath);
                return;
            }
            
            // 处理所有数据源目录
            for (File dataSourceDir : historyDir.listFiles(File::isDirectory)) {
                log.debug("处理数据源目录: {}", dataSourceDir.getName());
                
                // 处理所有频率目录
                for (File frequencyDir : dataSourceDir.listFiles(File::isDirectory)) {
                    String frequency = frequencyDir.getName();
                    int retentionDays = retentionMap.getOrDefault(frequency, 7);
                    
                    log.debug("处理频率目录: {}, 保留天数: {}", frequency, retentionDays);
                    
                    // 清理过期文件
                    cleanupExpiredFiles(frequencyDir.toPath(), retentionDays);
                }
            }
            
            log.info("历史性能数据清理完成");
        } catch (Exception e) {
            log.error("清理历史性能数据失败", e);
        }
    }
    
    /**
     * 清理指定目录中的过期文件
     */
    private void cleanupExpiredFiles(Path directory, int retentionDays) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        // 计算过期时间
        long cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(retentionDays);
        
        // 找出并删除过期文件
        try (Stream<Path> pathStream = Files.list(directory)) {
            pathStream
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(path -> {
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                        if (attrs.creationTime().toMillis() < cutoffTime) {
                            log.debug("删除过期文件: {}, 创建时间: {}", 
                                    path.getFileName(), 
                                    formatDateTime(attrs.creationTime().toMillis()));
                            Files.delete(path);
                        }
                    } catch (IOException e) {
                        log.error("无法删除文件: {}", path, e);
                    }
                });
        }
    }
    
    /**
     * 格式化日期时间
     */
    private String formatDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
