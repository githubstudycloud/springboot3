package com.example.platform.governance.metadata.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 元数据模块配置类
 */
@Configuration
@ComponentScan("com.example.platform.governance.metadata")
@EnableMongoRepositories("com.example.platform.governance.metadata.infrastructure.repository")
public class MetadataModuleConfig {
    
    // 可以在这里添加额外的配置
}
