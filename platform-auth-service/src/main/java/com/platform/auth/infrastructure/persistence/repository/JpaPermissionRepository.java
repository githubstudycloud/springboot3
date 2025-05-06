package com.platform.auth.infrastructure.persistence.repository;

import com.platform.auth.infrastructure.persistence.entity.PermissionEntity;
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
 * 权限JPA仓储接口
 * <p>
 * 基于Spring Data JPA实现的权限仓储
 * </p>
 */
@Repository
public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, String> {
    
    /**
     * 根据权限编码查询权限
     *
     * @param code 权限编码
     * @return 权限实体
     */
    Optional<PermissionEntity> findByCode(String code);
    
    /**
     * 根据权限名称查询权限
     *
     * @param name 权限名称
     * @return 权限实体
     */
    Optional<PermissionEntity> findByName(String name);
    
    /**
     * 根据URL和方法查询权限
     *
     * @param url 权限URL
     * @param method 权限方法
     * @return 权限实体
     */
    Optional<PermissionEntity> findByUrlAndMethod(String url, String method);
    
    /**
     * 根据父ID查询权限列表
     *
     * @param parentId 父权限ID
     * @return 权限实体列表
     */
    List<PermissionEntity> findByParentId(String parentId);
    
    /**
     * 根据父ID查询权限列表，并按排序字段排序
     *
     * @param parentId 父权限ID
     * @return 权限实体列表
     */
    List<PermissionEntity> findByParentIdOrderBySortAsc(String parentId);
    
    /**
     * 根据权限类型查询权限列表
     *
     * @param type 权限类型
     * @return 权限实体列表
     */
    List<PermissionEntity> findByType(String type);
    
    /**
     * 根据权限类型和父ID查询权限列表
     *
     * @param type 权限类型
     * @param parentId 父权限ID
     * @return 权限实体列表
     */
    List<PermissionEntity> findByTypeAndParentId(String type, String parentId);
    
    /**
     * 根据租户ID查询权限列表
     *
     * @param tenantId 租户ID
     * @return 权限实体列表
     */
    List<PermissionEntity> findByTenantId(String tenantId);
    
    /**
     * 根据租户ID分页查询权限
     *
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 权限实体分页
     */
    Page<PermissionEntity> findByTenantId(String tenantId, Pageable pageable);
    
    /**
     * 查询权限编码是否存在
     *
     * @param code 权限编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 查询权限名称是否存在
     *
     * @param name 权限名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据ID集合查询权限列表
     *
     * @param ids ID集合
     * @return 权限实体列表
     */
    Set<PermissionEntity> findByIdIn(Set<String> ids);
    
    /**
     * 更新权限状态
     *
     * @param permissionId 权限ID
     * @param status 状态
     * @return 影响行数
     */
    @Modifying
    @Query("update PermissionEntity p set p.status = :status where p.id = :permissionId")
    int updateStatus(@Param("permissionId") String permissionId, @Param("status") Integer status);
    
    /**
     * 软删除权限
     *
     * @param permissionId 权限ID
     * @return 影响行数
     */
    @Modifying
    @Query("update PermissionEntity p set p.status = -1 where p.id = :permissionId")
    int softDelete(@Param("permissionId") String permissionId);
    
    /**
     * 根据状态查询权限
     *
     * @param status 状态
     * @return 权限实体列表
     */
    List<PermissionEntity> findByStatus(Integer status);
    
    /**
     * 查询所有菜单权限
     *
     * @return 权限实体列表
     */
    @Query("select p from PermissionEntity p where p.type = 'menu' and p.status = 1 order by p.sort asc")
    List<PermissionEntity> findAllMenus();
}
