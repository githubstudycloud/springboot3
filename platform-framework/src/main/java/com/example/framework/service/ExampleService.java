package com.example.framework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 示例服务类，展示Spring Boot 3.2.9推荐的构造器注入方式
 *
 * @author platform
 * @since 1.0.0
 */
@Slf4j
@Service
public class ExampleService {
    
    private final ExampleRepository repository;
    
    /**
     * 推荐使用构造器注入方式
     * Spring Boot 3.2+已优化构造器注入，无需显式添加@Autowired注解
     *
     * @param repository 示例仓库
     */
    public ExampleService(ExampleRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 示例方法，展示事务和虚拟线程支持
     *
     * @param id 实体ID
     * @return 实体名称
     */
    @Transactional(readOnly = true)
    public String getEntityName(Long id) {
        log.info("当前线程: {}", Thread.currentThread());
        return repository.findNameById(id);
    }
    
    /**
     * 内部接口，用于展示
     */
    public interface ExampleRepository {
        /**
         * 根据ID查询名称
         *
         * @param id 实体ID
         * @return 实体名称
         */
        String findNameById(Long id);
    }
}
