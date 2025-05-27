package com.framework.excel.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Configuration
@MapperScan("com.framework.excel.mapper")
public class MybatisConfig {
} 