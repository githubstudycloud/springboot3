package com.platform.scheduler.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.platform.scheduler.service.impl.LogServiceImpl;

/**
 * 日志表初始化器
 * 
 * @author platform
 */
@Component
public class LogTableInitializer implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(LogTableInitializer.class);
    
    @Autowired
    private LogServiceImpl logService;
    
    @Value("${scheduler.log.retention.months:12}")
    private int logRetentionMonths;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 应用启动时初始化当前月份的日志表
        initializeLogTables();
        
        // 每天凌晨2点检查并初始化日志表
        scheduleLogTableMaintenance();
    }
    
    /**
     * 初始化日志表
     */
    private void initializeLogTables() {
        try {
            logger.info("开始初始化日志表...");
            
            // 初始化当前月份的日志表
            boolean success = logService.initCurrentMonthLogTables();
            
            if (success) {
                logger.info("日志表初始化成功");
            } else {
                logger.error("日志表初始化失败");
            }
            
            // 清理过期日志表
            int cleanedCount = logService.cleanupExpiredLogTables(logRetentionMonths);
            logger.info("清理过期日志表: {} 个", cleanedCount);
            
        } catch (Exception e) {
            logger.error("初始化日志表出错", e);
        }
    }
    
    /**
     * 调度日志表维护任务
     */
    private void scheduleLogTableMaintenance() {
        // 计算到凌晨2点的延迟时间
        long initialDelay = calculateInitialDelay();
        
        // 调度任务，每天凌晨2点执行
        scheduler.scheduleAtFixedRate(this::initializeLogTables, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        
        logger.info("日志表维护任务已调度，首次执行将在 {} 小时后", initialDelay / 3600);
    }
    
    /**
     * 计算到凌晨2点的延迟时间（秒）
     */
    private long calculateInitialDelay() {
        long currentTimeMillis = System.currentTimeMillis();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        
        // 设置为明天的凌晨2点
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 2);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        
        return (calendar.getTimeInMillis() - currentTimeMillis) / 1000;
    }
}
