package com.platform.auth.domain.repository;

import com.platform.auth.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 * <p>
 * 遵循DDD设计，定义仓储接口，不依赖于具体的ORM框架
 * </p>
 */
public interface UserRepository {
    
    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User save(User user);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    Optional<User> findById(String id);
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户对象
     */
    Optional<User> findByMobile(String mobile);
    
    /**
     * 根据租户ID查询用户列表
     *
     * @param tenantId 租户ID
     * @return 用户列表
     */
    List<User> findByTenantId(String tenantId);
    
    /**
     * 分页查询用户
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 用户列表
     */
    List<User> findAll(int page, int size);
    
    /**
     * 查询用户总数
     *
     * @return 用户总数
     */
    long count();
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteById(String id);
    
    /**
     * 查询用户是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 查询邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 查询手机号是否存在
     *
     * @param mobile 手机号
     * @return 是否存在
     */
    boolean existsByMobile(String mobile);
}
