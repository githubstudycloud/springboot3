package com.platform.auth.infrastructure.persistence.repository;

import com.platform.auth.infrastructure.persistence.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 租户JPA仓储接口
 * <p>
 * 基于Spring Data JPA实现的租户仓储
 * </p>
 */
@Repository
public interface JpaTenantRepository extends JpaRepository<TenantEntity, String> {
    
    /**
     * 根据租户编码查询租户
     *
     * @param code 租户编码
     * @return 租户实体
     */
    Optional<TenantEntity> findByCode(String code);
    
    /**
     * 根据租户名称查询租户
     *
     * @param name 租户名称
     * @return 租户实体
     */
    Optional<TenantEntity> findByName(String name);
    
    /**
     * 查询租户编码是否存在
     *
     * @param code 租户编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 查询租户名称是否存在
     *
     * @param name 租户名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 更新租户状态
     *
     * @param tenantId 租户ID
     * @param status 状态
     * @return 影响行数
     */
    @Modifying
    @Query("update TenantEntity t set t.status = :status where t.id = :tenantId")
    int updateStatus(@Param("tenantId") String tenantId, @Param("status") Integer status);
    
    /**
     * 软删除租户
     *
     * @param tenantId 租户ID
     * @return 影响行数
     */
    @Modifying
    @Query("update TenantEntity t set t.status = -1 where t.id = :tenantId")
    int softDelete(@Param("tenantId") String tenantId);
    
    /**
     * 根据状态查询租户
     *
     * @param status 状态
     * @return 租户实体列表
     */
    List<TenantEntity> findByStatus(Integer status);
    
    /**
     * 查询所有有效的租户
     *
     * @return 租户实体列表
     */
    @Query("select t from TenantEntity t where t.status = 1 and (t.expireTime is null or t.expireTime > :now)")
    List<TenantEntity> findAllEnabled(@Param("now") LocalDateTime now);
    
    /**
     * 更新租户用户数量
     *
     * @param tenantId 租户ID
     * @param count 用户数量
     * @return 影响行数
     */
    @Modifying
    @Query("update TenantEntity t set t.currentUserCount = :count where t.id = :tenantId")
    int updateUserCount(@Param("tenantId") String tenantId, @Param("count") Integer count);
    
    /**
     * 增加租户用户数量
     *
     * @param tenantId 租户ID
     * @return 影响行数
     */
    @Modifying
    @Query("update TenantEntity t set t.currentUserCount = t.currentUserCount + 1 where t.id = :tenantId")
    int incrementUserCount(@Param("tenantId") String tenantId);
    
    /**
     * 减少租户用户数量
     *
     * @param tenantId 租户ID
     * @return 影响行数
     */
    @Modifying
    @Query("update TenantEntity t set t.currentUserCount = t.currentUserCount - 1 where t.id = :tenantId and t.currentUserCount > 0")
    int decrementUserCount(@Param("tenantId") String tenantId);
}
