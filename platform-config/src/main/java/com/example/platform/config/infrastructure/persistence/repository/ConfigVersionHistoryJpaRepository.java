package com.example.platform.config.infrastructure.persistence.repository;

import com.example.platform.config.infrastructure.persistence.entity.ConfigVersionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 配置版本历史JPA仓储接口
 */
@Repository
public interface ConfigVersionHistoryJpaRepository extends JpaRepository<ConfigVersionHistoryEntity, Long> {

    /**
     * 根据配置项ID查询版本历史
     */
    List<ConfigVersionHistoryEntity> findByConfigItemIdOrderByVersionDesc(Long configItemId);

    /**
     * 根据配置项dataId和group查询版本历史
     */
    @Query("SELECT h FROM ConfigVersionHistoryEntity h " +
           "WHERE h.dataId = :dataId AND h.group = :group AND h.environment = :environment " +
           "ORDER BY h.version DESC")
    List<ConfigVersionHistoryEntity> findByDataIdAndGroupAndEnvironmentOrderByVersionDesc(
            @Param("dataId") String dataId, 
            @Param("group") String group, 
            @Param("environment") String environment);

    /**
     * 查询特定版本的配置历史
     */
    Optional<ConfigVersionHistoryEntity> findByConfigItemIdAndVersion(Long configItemId, Integer version);

    /**
     * 获取配置项的最新版本号
     */
    @Query("SELECT MAX(h.version) FROM ConfigVersionHistoryEntity h WHERE h.configItemId = :configItemId")
    Integer getLatestVersionNumber(@Param("configItemId") Long configItemId);

    /**
     * 删除指定数量以外的旧版本
     */
    @Modifying
    @Query(value = "DELETE FROM t_config_version_history WHERE config_item_id = :configItemId " +
                  "AND version NOT IN (SELECT version FROM " +
                  "(SELECT version FROM t_config_version_history WHERE config_item_id = :configItemId " +
                  "ORDER BY version DESC LIMIT :keepVersionsCount) AS temp)", nativeQuery = true)
    void deleteOldVersions(@Param("configItemId") Long configItemId, 
                          @Param("keepVersionsCount") Integer keepVersionsCount);
}
