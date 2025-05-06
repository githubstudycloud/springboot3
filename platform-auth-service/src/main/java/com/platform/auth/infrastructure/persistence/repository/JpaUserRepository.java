package com.platform.auth.infrastructure.persistence.repository;

import com.platform.auth.infrastructure.persistence.entity.UserEntity;
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
 * 用户JPA仓储接口
 * <p>
 * 基于Spring Data JPA实现的用户仓储
 * </p>
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    Optional<UserEntity> findByUsername(String username);
    
    /**
     * 根据电子邮件查询用户
     *
     * @param email 电子邮件
     * @return 用户实体
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户实体
     */
    Optional<UserEntity> findByMobile(String mobile);
    
    /**
     * 根据租户ID查询用户列表
     *
     * @param tenantId 租户ID
     * @return 用户实体列表
     */
    List<UserEntity> findByTenantId(String tenantId);
    
    /**
     * 根据租户ID分页查询用户
     *
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 用户实体分页
     */
    Page<UserEntity> findByTenantId(String tenantId, Pageable pageable);
    
    /**
     * 查询用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 查询电子邮件是否存在
     *
     * @param email 电子邮件
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
    
    /**
     * 更新用户最后登录信息
     *
     * @param userId 用户ID
     * @param lastLoginIp 最后登录IP
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    @Modifying
    @Query("update UserEntity u set u.lastLoginIp = :lastLoginIp, u.lastLoginTime = :lastLoginTime where u.id = :userId")
    int updateLastLogin(@Param("userId") String userId, @Param("lastLoginIp") String lastLoginIp, @Param("lastLoginTime") LocalDateTime lastLoginTime);
    
    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 影响行数
     */
    @Modifying
    @Query("update UserEntity u set u.status = :status where u.id = :userId")
    int updateStatus(@Param("userId") String userId, @Param("status") Integer status);
    
    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param password 密码
     * @return 影响行数
     */
    @Modifying
    @Query("update UserEntity u set u.password = :password where u.id = :userId")
    int updatePassword(@Param("userId") String userId, @Param("password") String password);
    
    /**
     * 软删除用户
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Modifying
    @Query("update UserEntity u set u.status = -1 where u.id = :userId")
    int softDelete(@Param("userId") String userId);
    
    /**
     * 根据状态查询用户
     *
     * @param status 状态
     * @return 用户实体列表
     */
    List<UserEntity> findByStatus(Integer status);
    
    /**
     * 查询指定租户下的用户数量
     *
     * @param tenantId 租户ID
     * @return 用户数量
     */
    long countByTenantId(String tenantId);
}
