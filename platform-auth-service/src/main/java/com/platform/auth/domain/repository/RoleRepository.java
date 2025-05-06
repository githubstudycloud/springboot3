package com.platform.auth.domain.repository;

import com.platform.auth.domain.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 角色仓储接口
 * <p>
 * 遵循DDD设计，定义仓储接口，不依赖于具体的ORM框架
 * </p>
 */
public interface RoleRepository {
    
    /**
     * 保存角色
     *
     * @param role 角色对象
     * @return 保存后的角色对象
     */
    Role save(Role role);
    
    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色对象
     */
    Optional<Role> findById(String id);
    
    /**
     * 根据编码查询角色
     *
     * @param code 角色编码
     * @return 角色对象
     */
    Optional<Role> findByCode(String code);
    
    /**
     * 根据名称查询角色
     *
     * @param name 角色名称
     * @return 角色对象
     */
    Optional<Role> findByName(String name);
    
    /**
     * 根据租户ID查询角色列表
     *
     * @param tenantId 租户ID
     * @return 角色列表
     */
    List<Role> findByTenantId(String tenantId);
    
    /**
     * 分页查询角色
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 角色列表
     */
    List<Role> findAll(int page, int size);
    
    /**
     * 查询角色总数
     *
     * @return 角色总数
     */
    long count();
    
    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteById(String id);
    
    /**
     * 查询角色是否存在
     *
     * @param code 角色编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 查询角色是否存在
     *
     * @param name 角色名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据多个ID查询角色列表
     *
     * @param ids 角色ID集合
     * @return 角色列表
     */
    Set<Role> findByIdIn(Set<String> ids);
}
