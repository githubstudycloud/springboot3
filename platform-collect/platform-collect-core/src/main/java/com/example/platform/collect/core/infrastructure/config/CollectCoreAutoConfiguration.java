package com.example.platform.collect.core.infrastructure.config;

import com.example.platform.collect.core.application.service.CollectService;
import com.example.platform.collect.core.application.service.impl.CollectServiceImpl;
import com.example.platform.collect.core.domain.service.PipelineEngine;
import com.example.platform.collect.core.domain.service.WatermarkManager;
import com.example.platform.collect.core.domain.service.impl.DefaultPipelineEngine;
import com.example.platform.collect.core.domain.service.impl.DefaultWatermarkManager;
import com.example.platform.collect.core.infrastructure.adapter.DataGovernanceAdapter;
import com.example.platform.collect.core.infrastructure.adapter.SchedulerAdapter;
import com.example.platform.collect.core.infrastructure.adapter.impl.DefaultDataGovernanceAdapter;
import com.example.platform.collect.core.infrastructure.adapter.impl.DefaultSchedulerAdapter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 采集框架核心配置类
 * 提供框架核心组件的自动配置
 */
@Configuration
@ComponentScan("com.example.platform.collect.core")
@EnableConfigurationProperties(CollectProperties.class)
public class CollectCoreAutoConfiguration {

    /**
     * 配置流水线执行引擎
     *
     * @param watermarkManager 水印管理器
     * @return 流水线执行引擎
     */
    @Bean
    @ConditionalOnMissingBean
    public PipelineEngine pipelineEngine(WatermarkManager watermarkManager) {
        return new DefaultPipelineEngine(watermarkManager);
    }

    /**
     * 配置水印管理器
     *
     * @param properties 采集框架配置属性
     * @return 水印管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public WatermarkManager watermarkManager(CollectProperties properties) {
        return new DefaultWatermarkManager(properties.getWatermark());
    }

    /**
     * 配置调度系统适配器
     *
     * @param properties 采集框架配置属性
     * @return 调度系统适配器
     */
    @Bean
    @ConditionalOnMissingBean
    public SchedulerAdapter schedulerAdapter(CollectProperties properties) {
        return new DefaultSchedulerAdapter(properties.getScheduler());
    }

    /**
     * 配置数据治理适配器
     *
     * @param properties 采集框架配置属性
     * @return 数据治理适配器
     */
    @Bean
    @ConditionalOnMissingBean
    public DataGovernanceAdapter dataGovernanceAdapter(CollectProperties properties) {
        return new DefaultDataGovernanceAdapter(properties.getDataGovernance());
    }

    /**
     * 配置采集服务
     *
     * @param pipelineEngine 流水线执行引擎
     * @param schedulerAdapter 调度系统适配器
     * @param dataGovernanceAdapter 数据治理适配器
     * @return 采集服务
     */
    @Bean
    @ConditionalOnMissingBean
    public CollectService collectService(
            PipelineEngine pipelineEngine,
            SchedulerAdapter schedulerAdapter,
            DataGovernanceAdapter dataGovernanceAdapter) {
        return new CollectServiceImpl(pipelineEngine, schedulerAdapter, dataGovernanceAdapter);
    }
}
