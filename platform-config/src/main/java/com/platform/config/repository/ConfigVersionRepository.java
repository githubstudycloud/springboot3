package com.platform.config.repository;

import com.platform.config.entity.ConfigVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 配置版本Repository
 * 
 * @author Platform Team
 * @since 2.0.0
 */
@Repository
public interface ConfigVersionRepository extends JpaRepository<ConfigVersion, Long> {

    /**
     * 根据应用和环境查找配置版本
     */
    List<ConfigVersion> findByApplicationAndProfileOrderByCreateTimeDesc(String application, String profile);

    /**
     * 根据应用和环境分页查找配置版本
     */
    Page<ConfigVersion> findByApplicationAndProfileOrderByCreateTimeDesc(
            String application, String profile, Pageable pageable);

    /**
     * 查找当前激活的版本
     */
    Optional<ConfigVersion> findByApplicationAndProfileAndActiveTrue(String application, String profile);

    /**
     * 根据版本号查找
     */
    Optional<ConfigVersion> findByVersion(String version);

    /**
     * 根据标签查找版本
     */
    List<ConfigVersion> findByTagOrderByCreateTimeDesc(String tag);

    /**
     * 查找指定时间范围内的版本
     */
    @Query("SELECT cv FROM ConfigVersion cv WHERE cv.application = :application " +
           "AND cv.profile = :profile AND cv.createTime BETWEEN :startTime AND :endTime " +
           "ORDER BY cv.createTime DESC")
    List<ConfigVersion> findVersionsByTimeRange(@Param("application") String application,
                                               @Param("profile") String profile,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查找特定操作员的版本
     */
    Page<ConfigVersion> findByOperatorOrderByCreateTimeDesc(String operator, Pageable pageable);

    /**
     * 统计版本数量
     */
    long countByApplicationAndProfile(String application, String profile);

    /**
     * 删除指定时间之前的版本（用于清理历史数据）
     */
    @Modifying
    @Query("DELETE FROM ConfigVersion cv WHERE cv.createTime < :beforeTime AND cv.active = false")
    int deleteVersionsBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 将所有版本设置为非激活状态
     */
    @Modifying
    @Query("UPDATE ConfigVersion cv SET cv.active = false WHERE cv.application = :application AND cv.profile = :profile")
    int deactivateAllVersions(@Param("application") String application, @Param("profile") String profile);

    /**
     * 查找内容哈希值相同的版本（用于去重）
     */
    List<ConfigVersion> findByContentHashOrderByCreateTimeDesc(String contentHash);

    /**
     * 根据父版本ID查找子版本
     */
    List<ConfigVersion> findByParentVersionIdOrderByCreateTimeDesc(Long parentVersionId);
} 