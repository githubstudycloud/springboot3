package com.platform.auth.domain.repository;

import com.platform.auth.domain.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限仓储接口
 * <p>
 * 遵循DDD设计，定义仓储接口，不依赖于具体的ORM框架
 * </p>
 */
public interface PermissionRepository {
    
    /**
     * 保存权限
     *
     * @param permission 权限对象
     * @return 保存后的权限对象
     */
    Permission save(Permission permission);
    
    /**
     * 根据ID查询权限
     *
     * @param id 权限ID
     * @return 权限对象
     */
    Optional<Permission> findById(String id);
    
    /**
     * 根据编码查询权限
     *
     * @param code 权限编码
     * @return 权限对象
     */
    Optional<Permission> findByCode(String code);
    
    /**
     * 根据名称查询权限
     *
     * @param name 权限名称
     * @return 权限对象
     */
    Optional<Permission> findByName(String name);
    
    /**
     * 根据URL和方法查询权限
     *
     * @param url 权限URL
     * @param method 权限方法
     * @return 权限对象
     */
    Optional<Permission> findByUrlAndMethod(String url, String method);
    
    /**
     * 根据父ID查询权限列表
     *
     * @param parentId 父权限ID
     * @return 权限列表
     */
    List<Permission> findByParentId(String parentId);
    
    /**
     * 根据权限类型查询权限列表
     *
     * @param type 权限类型
     * @return 权限列表
     */
    List<Permission> findByType(String type);
    
    /**
     * 根据租户ID查询权限列表
     *
     * @param tenantId 租户ID
     * @return 权限列表
     */
    List<Permission> findByTenantId(String tenantId);
    
    /**
     * 分页查询权限
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 权限列表
     */
    List<Permission> findAll(int page, int size);
    
    /**
     * 查询权限总数
     *
     * @return 权限总数
     */
    long count();
    
    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deleteById(String id);
    
    /**
     * 查询权限是否存在
     *
     * @param code 权限编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 查询权限是否存在
     *
     * @param name 权限名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据多个ID查询权限列表
     *
     * @param ids 权限ID集合
     * @return 权限列表
     */
    Set<Permission> findByIdIn(Set<String> ids);
}
