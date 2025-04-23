package com.platform.scheduler.config.datasource.multi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.platform.scheduler.service.DataSourceService;

/**
 * 数据源初始化器
 * 
 * @author platform
 */
@Component
public class DataSourceInitializer implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializer.class);
    
    @Autowired
    private DataSourceService dataSourceService;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            // 加载数据源配置
            logger.info("开始加载数据源配置...");
            boolean loaded = dataSourceService.loadDataSourceConfig();
            
            if (loaded) {
                logger.info("数据源配置加载成功");
                
                // 检查所有数据源连接
                dataSourceService.checkAllDataSources().forEach((id, status) -> {
                    if (status) {
                        logger.info("数据源连接正常: {}", id);
                    } else {
                        logger.error("数据源连接异常: {}", id);
                    }
                });
            } else {
                logger.warn("未加载数据源配置，使用默认配置");
            }
        } catch (Exception e) {
            logger.error("初始化数据源失败", e);
        }
    }
}
