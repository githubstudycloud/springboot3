package com.framework.excel.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 *
 * @author Framework
 * @since 1.0.0
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.framework.excel.mapper")
public class MyBatisConfig {
    // MyBatis configuration
}
