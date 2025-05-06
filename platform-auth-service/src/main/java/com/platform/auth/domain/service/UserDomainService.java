package com.platform.auth.domain.service;

import com.platform.auth.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 用户领域服务接口
 * <p>
 * 负责用户管理相关的核心业务逻辑
 * </p>
 */
public interface UserDomainService {
    
    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建成功的用户
     */
    User createUser(User user);
    
    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 更新后的用户
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(String userId);
    
    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    Optional<User> findUser(String userId);
    
    /**
     * 查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findUserByUsername(String username);
    
    /**
     * 查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findUserByEmail(String email);
    
    /**
     * 查询用户
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    Optional<User> findUserByMobile(String mobile);
    
    /**
     * 分页查询用户
     *
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    List<User> findUsers(int page, int size);
    
    /**
     * 查询租户下的用户
     *
     * @param tenantId 租户ID
     * @return 用户列表
     */
    List<User> findUsersByTenant(String tenantId);
    
    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 新的随机密码
     */
    String resetPassword(String userId);
    
    /**
     * 启用用户
     *
     * @param userId 用户ID
     */
    void enableUser(String userId);
    
    /**
     * 禁用用户
     *
     * @param userId 用户ID
     */
    void disableUser(String userId);
    
    /**
     * 锁定用户
     *
     * @param userId 用户ID
     */
    void lockUser(String userId);
    
    /**
     * 解锁用户
     *
     * @param userId 用户ID
     */
    void unlockUser(String userId);
    
    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     */
    void assignRoles(String userId, Set<String> roleIds);
    
    /**
     * 移除用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     */
    void removeRoles(String userId, Set<String> roleIds);
    
    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 检查手机号是否已存在
     *
     * @param mobile 手机号
     * @return 是否存在
     */
    boolean isMobileExists(String mobile);
}
