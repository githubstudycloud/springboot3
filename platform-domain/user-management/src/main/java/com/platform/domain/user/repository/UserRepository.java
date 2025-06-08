package com.platform.domain.user.repository;

import com.platform.domain.user.entity.User;
import com.platform.domain.user.valueobject.UserId;
import com.platform.domain.user.valueobject.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 * DDD设计模式 - 仓储模式
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户ID查找用户
     */
    Optional<User> findByUserId(UserId userId);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找用户
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * 根据状态分页查找用户
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    /**
     * 查找指定时间之后创建的用户
     */
    List<User> findByCreatedAtAfter(LocalDateTime dateTime);
    
    /**
     * 查找指定时间之后登录的用户
     */
    List<User> findByLastLoginAtAfter(LocalDateTime dateTime);
    
    /**
     * 根据用户名模糊查询
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计各状态用户数量
     */
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countByStatus();
    
    /**
     * 查找活跃用户（最近30天登录过）
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :since AND u.status = :status")
    List<User> findActiveUsers(@Param("since") LocalDateTime since, @Param("status") UserStatus status);
    
    /**
     * 查找长时间未登录的用户
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :before OR u.lastLoginAt IS NULL")
    List<User> findInactiveUsers(@Param("before") LocalDateTime before);
    
    /**
     * 根据真实姓名查找用户
     */
    List<User> findByRealNameContaining(String realName);
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * 删除指定时间之前创建的已删除状态用户
     */
    @Query("DELETE FROM User u WHERE u.status = :status AND u.createdAt < :before")
    void deleteOldDeletedUsers(@Param("status") UserStatus status, @Param("before") LocalDateTime before);
} 