package com.platform.auth.domain.repository;

import com.platform.auth.domain.model.Tenant;

import java.util.List;
import java.util.Optional;

/**
 * 租户仓储接口
 * <p>
 * 遵循DDD设计，定义仓储接口，不依赖于具体的ORM框架
 * </p>
 */
public interface TenantRepository {
    
    /**
     * 保存租户
     *
     * @param tenant 租户对象
     * @return 保存后的租户对象
     */
    Tenant save(Tenant tenant);
    
    /**
     * 根据ID查询租户
     *
     * @param id 租户ID
     * @return 租户对象
     */
    Optional<Tenant> findById(String id);
    
    /**
     * 根据编码查询租户
     *
     * @param code 租户编码
     * @return 租户对象
     */
    Optional<Tenant> findByCode(String code);
    
    /**
     * 根据名称查询租户
     *
     * @param name 租户名称
     * @return 租户对象
     */
    Optional<Tenant> findByName(String name);
    
    /**
     * 分页查询租户
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 租户列表
     */
    List<Tenant> findAll(int page, int size);
    
    /**
     * 查询租户总数
     *
     * @return 租户总数
     */
    long count();
    
    /**
     * 删除租户
     *
     * @param id 租户ID
     */
    void deleteById(String id);
    
    /**
     * 查询租户是否存在
     *
     * @param code 租户编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 查询租户是否存在
     *
     * @param name 租户名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 查询所有有效的租户
     *
     * @return 租户列表
     */
    List<Tenant> findAllEnabled();
    
    /**
     * 根据状态查询租户列表
     *
     * @param status 状态
     * @return 租户列表
     */
    List<Tenant> findByStatus(Integer status);
    
    /**
     * 更新租户用户数量
     *
     * @param id 租户ID
     * @param count 用户数量
     */
    void updateUserCount(String id, Integer count);
}
