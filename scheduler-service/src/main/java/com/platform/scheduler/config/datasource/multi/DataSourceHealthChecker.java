package com.platform.scheduler.config.datasource.multi;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.platform.scheduler.service.DataSourceService;
import com.platform.scheduler.util.SchedulerThreadFactory;

/**
 * 数据源健康检查器
 * 
 * @author platform
 */
@Component
public class DataSourceHealthChecker implements InitializingBean, DisposableBean {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceHealthChecker.class);
    
    @Autowired
    private DataSourceService dataSourceService;
    
    @Value("${scheduler.datasource.health-check-rate:60000}")
    private long healthCheckRate;
    
    private ScheduledExecutorService scheduler;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建调度器
        scheduler = new ScheduledThreadPoolExecutor(1, new SchedulerThreadFactory("DataSourceHealthChecker"));
        
        // 启动健康检查任务
        if (healthCheckRate > 0) {
            scheduler.scheduleAtFixedRate(this::checkDataSourceHealth, 
                    healthCheckRate, healthCheckRate, TimeUnit.MILLISECONDS);
            
            logger.info("数据源健康检查已启动，检查频率: {} 毫秒", healthCheckRate);
        }
    }
    
    @Override
    public void destroy() throws Exception {
        // 关闭调度器
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            logger.info("数据源健康检查已停止");
        }
    }
    
    /**
     * 检查数据源健康状态
     */
    private void checkDataSourceHealth() {
        try {
            logger.debug("开始检查数据源健康状态...");
            
            // 检查所有数据源
            Map<String, Boolean> results = dataSourceService.checkAllDataSources();
            
            // 输出检查结果
            results.forEach((id, status) -> {
                if (!status) {
                    logger.error("数据源连接异常: {}", id);
                } else {
                    logger.debug("数据源连接正常: {}", id);
                }
            });
            
        } catch (Exception e) {
            logger.error("检查数据源健康状态异常", e);
        }
    }
}
