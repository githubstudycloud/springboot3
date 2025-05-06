package com.platform.auth.infrastructure.persistence.repository;

import com.platform.auth.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 角色JPA仓储接口
 * <p>
 * 基于Spring Data JPA实现的角色仓储
 * </p>
 */
@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, String> {
    
    /**
     * 根据角色编码查询角色
     *
     * @param code 角色编码
     * @return 角色实体
     */
    Optional<RoleEntity> findByCode(String code);
    
    /**
     * 根据角色名称查询角色
     *
     * @param name 角色名称
     * @return 角色实体
     */
    Optional<RoleEntity> findByName(String name);
    
    /**
     * 根据租户ID查询角色列表
     *
     * @param tenantId 租户ID
     * @return 角色实体列表
     */
    List<RoleEntity> findByTenantId(String tenantId);
    
    /**
     * 根据租户ID分页查询角色
     *
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 角色实体分页
     */
    Page<RoleEntity> findByTenantId(String tenantId, Pageable pageable);
    
    /**
     * 查询角色编码是否存在
     *
     * @param code 角色编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 查询角色名称是否存在
     *
     * @param name 角色名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据ID集合查询角色列表
     *
     * @param ids ID集合
     * @return 角色实体列表
     */
    Set<RoleEntity> findByIdIn(Set<String> ids);
    
    /**
     * 更新角色状态
     *
     * @param roleId 角色ID
     * @param status 状态
     * @return 影响行数
     */
    @Modifying
    @Query("update RoleEntity r set r.status = :status where r.id = :roleId")
    int updateStatus(@Param("roleId") String roleId, @Param("status") Integer status);
    
    /**
     * 软删除角色
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Modifying
    @Query("update RoleEntity r set r.status = -1 where r.id = :roleId")
    int softDelete(@Param("roleId") String roleId);
    
    /**
     * 根据状态查询角色
     *
     * @param status 状态
     * @return 角色实体列表
     */
    List<RoleEntity> findByStatus(Integer status);
    
    /**
     * 根据角色编码集合查询角色列表
     *
     * @param codes 角色编码集合
     * @return 角色实体列表
     */
    List<RoleEntity> findByCodeIn(Set<String> codes);
}
