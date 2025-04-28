package com.example.framework.config;

import com.example.framework.domain.repository.InventoryRepository;
import com.example.framework.infrastructure.persistence.InventoryRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 资源库配置类
 * 用于显式定义资源库Bean
 */
@Configuration
public class RepositoryConfig {

    /**
     * 库存资源库Bean
     * 
     * @return 库存资源库实现
     */
    @Bean
    public InventoryRepository inventoryRepository() {
        return new InventoryRepositoryImpl();
    }
}
