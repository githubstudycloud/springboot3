package com.platform.scheduler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.platform.scheduler.core.log.LogManager;
import com.platform.scheduler.core.scheduler.TaskScheduler;
import com.platform.scheduler.core.status.TaskStatusManager;

/**
 * 调度器初始化组件
 * 在应用启动后执行初始化操作
 * 
 * @author platform
 */
@Component
@Order(1)
public class SchedulerInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerInitializer.class);
    
    @Autowired
    private TaskScheduler taskScheduler;
    
    @Autowired
    private LogManager logManager;
    
    @Autowired
    private TaskStatusManager taskStatusManager;

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("初始化调度器...");
            
            // 确保日志表存在
            logManager.ensureFutureLogTables();
            
            // 检查超时任务
            taskStatusManager.checkTimeoutTasks();
            
            // 启动任务调度器
            taskScheduler.start();
            
            logger.info("调度器初始化完成");
        } catch (Exception e) {
            logger.error("调度器初始化异常", e);
            throw e;
        }
    }
}
