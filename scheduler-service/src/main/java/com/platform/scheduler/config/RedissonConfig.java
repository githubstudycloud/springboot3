package com.platform.scheduler.config;

import java.util.List;
import java.util.stream.Collectors;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类，用于分布式锁等功能
 * 
 * @author platform
 */
@Configuration
public class RedissonConfig {
    
    /**
     * 配置RedissonClient
     * 
     * @param redisProperties Redis属性配置
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        
        // 单节点配置
        if (redisProperties.getCluster() == null && redisProperties.getSentinel() == null) {
            String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
            config.useSingleServer()
                .setAddress(address)
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase());
        }
        
        // 集群配置
        if (redisProperties.getCluster() != null) {
            List<String> nodes = redisProperties.getCluster().getNodes();
            List<String> addresses = nodes.stream()
                .map(node -> "redis://" + node)
                .collect(Collectors.toList());
            
            config.useClusterServers()
                .addNodeAddress(addresses.toArray(new String[0]))
                .setPassword(redisProperties.getPassword());
        }
        
        // 哨兵配置
        if (redisProperties.getSentinel() != null) {
            Sentinel sentinel = redisProperties.getSentinel();
            config.useSentinelServers()
                .setMasterName(sentinel.getMaster())
                .addSentinelAddress(getSentinelAddresses(sentinel))
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase());
        }
        
        return Redisson.create(config);
    }
    
    /**
     * 获取哨兵地址列表
     * 
     * @param sentinel 哨兵配置
     * @return 哨兵地址数组
     */
    private String[] getSentinelAddresses(Sentinel sentinel) {
        List<String> addresses = sentinel.getNodes().stream()
            .map(node -> "redis://" + node)
            .collect(Collectors.toList());
        return addresses.toArray(new String[0]);
    }
}
