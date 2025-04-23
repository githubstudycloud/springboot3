package com.platform.scheduler.core.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 负载均衡策略工厂
 * 
 * @author platform
 */
@Component
public class LoadBalanceStrategyFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadBalanceStrategyFactory.class);
    
    @Value("${scheduler.cluster.load-balance-strategy:RoundRobin}")
    private String defaultStrategyName;
    
    @Autowired
    private List<LoadBalanceStrategy> strategies;
    
    /**
     * 策略映射
     */
    private final Map<String, LoadBalanceStrategy> strategyMap = new HashMap<>();
    
    /**
     * 默认策略
     */
    private LoadBalanceStrategy defaultStrategy;
    
    @PostConstruct
    public void init() {
        // 将所有策略注册到映射中
        for (LoadBalanceStrategy strategy : strategies) {
            strategyMap.put(strategy.getName(), strategy);
            logger.info("注册负载均衡策略: {}", strategy.getName());
        }
        
        // 设置默认策略
        defaultStrategy = strategyMap.get(defaultStrategyName);
        if (defaultStrategy == null) {
            // 如果默认策略不存在，则使用第一个策略
            if (!strategies.isEmpty()) {
                defaultStrategy = strategies.get(0);
                logger.warn("默认负载均衡策略 {} 不存在，使用 {} 作为默认策略", 
                        defaultStrategyName, defaultStrategy.getName());
            } else {
                logger.error("没有可用的负载均衡策略");
            }
        } else {
            logger.info("默认负载均衡策略: {}", defaultStrategy.getName());
        }
    }
    
    /**
     * 获取负载均衡策略
     * 
     * @param name 策略名称
     * @return 负载均衡策略
     */
    public LoadBalanceStrategy getStrategy(String name) {
        LoadBalanceStrategy strategy = strategyMap.get(name);
        return strategy != null ? strategy : defaultStrategy;
    }
    
    /**
     * 获取默认负载均衡策略
     * 
     * @return 默认负载均衡策略
     */
    public LoadBalanceStrategy getDefaultStrategy() {
        return defaultStrategy;
    }
    
    /**
     * 获取所有负载均衡策略
     * 
     * @return 所有负载均衡策略
     */
    public Map<String, LoadBalanceStrategy> getAllStrategies() {
        return new HashMap<>(strategyMap);
    }
}
