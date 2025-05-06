package com.example.platform.config.infrastructure.persistence.repository;

import com.example.platform.config.infrastructure.persistence.entity.ConfigItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 配置项JPA仓储接口
 */
@Repository
public interface ConfigItemJpaRepository extends JpaRepository<ConfigItemEntity, Long> {

    /**
     * 根据dataId和group查询配置项
     */
    Optional<ConfigItemEntity> findByDataIdAndGroupAndEnvironment(String dataId, String group, String environment);

    /**
     * 根据分组和环境查询配置列表
     */
    List<ConfigItemEntity> findByGroupAndEnvironment(String group, String environment);

    /**
     * 根据环境查询所有配置
     */
    List<ConfigItemEntity> findByEnvironment(String environment);

    /**
     * 关键字搜索配置
     */
    @Query("SELECT c FROM ConfigItemEntity c WHERE " +
           "(c.dataId LIKE %:keyword% OR c.group LIKE %:keyword% OR c.description LIKE %:keyword%) " +
           "AND c.environment = :environment")
    List<ConfigItemEntity> search(@Param("keyword") String keyword, @Param("environment") String environment);
}
