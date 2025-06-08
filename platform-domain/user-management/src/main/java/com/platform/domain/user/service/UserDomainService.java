package com.platform.domain.user.service;

import com.platform.domain.user.entity.User;
import com.platform.domain.user.repository.UserRepository;
import com.platform.domain.user.valueobject.UserId;
import com.platform.domain.user.valueobject.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户领域服务
 * DDD设计模式 - 领域服务
 * 封装复杂的业务逻辑和跨聚合的操作
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Service
@Transactional
public class UserDomainService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 创建新用户
     * 业务规则：用户名和邮箱必须唯一
     */
    public User createUser(String username, String password, String email) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名 '" + username + "' 已存在");
        }
        
        // 检查邮箱是否已存在
        if (email != null && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱 '" + email + "' 已存在");
        }
        
        // 创建用户
        User user = new User(username, password, email);
        return userRepository.save(user);
    }
    
    /**
     * 用户认证
     * 支持用户名或邮箱登录
     */
    public Optional<User> authenticateUser(String usernameOrEmail, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.validatePassword(password)) {
                user.login(); // 更新登录时间并发布事件
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * 更改用户密码
     */
    public void changeUserPassword(UserId userId, String oldPassword, String newPassword) {
        User user = findUserByIdOrThrow(userId);
        user.changePassword(oldPassword, newPassword);
        userRepository.save(user);
    }
    
    /**
     * 更新用户资料
     */
    public void updateUserProfile(UserId userId, String email, String phoneNumber, String realName) {
        User user = findUserByIdOrThrow(userId);
        
        // 如果邮箱发生变化，检查新邮箱是否已被使用
        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱 '" + email + "' 已被其他用户使用");
        }
        
        user.updateProfile(email, phoneNumber, realName);
        userRepository.save(user);
    }
    
    /**
     * 激活用户
     */
    public void activateUser(UserId userId) {
        User user = findUserByIdOrThrow(userId);
        user.activate();
        userRepository.save(user);
    }
    
    /**
     * 停用用户
     */
    public void deactivateUser(UserId userId) {
        User user = findUserByIdOrThrow(userId);
        user.deactivate();
        userRepository.save(user);
    }
    
    /**
     * 锁定用户
     */
    public void lockUser(UserId userId) {
        User user = findUserByIdOrThrow(userId);
        user.lock();
        userRepository.save(user);
    }
    
    /**
     * 解锁用户
     */
    public void unlockUser(UserId userId) {
        User user = findUserByIdOrThrow(userId);
        user.unlock();
        userRepository.save(user);
    }
    
    /**
     * 批量激活用户
     */
    public void batchActivateUsers(List<UserId> userIds) {
        for (UserId userId : userIds) {
            try {
                activateUser(userId);
            } catch (Exception e) {
                // 记录错误但继续处理其他用户
                // 在实际项目中应该使用日志框架
                System.err.println("激活用户失败: " + userId + ", 错误: " + e.getMessage());
            }
        }
    }
    
    /**
     * 批量停用用户
     */
    public void batchDeactivateUsers(List<UserId> userIds) {
        for (UserId userId : userIds) {
            try {
                deactivateUser(userId);
            } catch (Exception e) {
                System.err.println("停用用户失败: " + userId + ", 错误: " + e.getMessage());
            }
        }
    }
    
    /**
     * 清理长时间未登录的用户
     * 业务规则：超过指定天数未登录的用户自动停用
     */
    public int cleanupInactiveUsers(int daysThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysThreshold);
        List<User> inactiveUsers = userRepository.findInactiveUsers(threshold);
        
        int count = 0;
        for (User user : inactiveUsers) {
            if (user.getStatus() == UserStatus.ACTIVE) {
                user.deactivate();
                userRepository.save(user);
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * 获取用户统计信息
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        List<Object[]> statusCounts = userRepository.countByStatus();
        
        long totalUsers = 0;
        long activeUsers = 0;
        long inactiveUsers = 0;
        long lockedUsers = 0;
        
        for (Object[] row : statusCounts) {
            UserStatus status = (UserStatus) row[0];
            Long count = (Long) row[1];
            totalUsers += count;
            
            switch (status) {
                case ACTIVE -> activeUsers = count;
                case INACTIVE -> inactiveUsers = count;
                case LOCKED -> lockedUsers = count;
            }
        }
        
        return new UserStatistics(totalUsers, activeUsers, inactiveUsers, lockedUsers);
    }
    
    /**
     * 检查用户是否可以执行特定操作
     */
    @Transactional(readOnly = true)
    public boolean canUserPerformOperation(UserId userId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        return userOpt.map(User::canPerformOperation).orElse(false);
    }
    
    private User findUserByIdOrThrow(UserId userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
    }
    
    /**
     * 用户统计信息
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long inactiveUsers;
        private final long lockedUsers;
        
        public UserStatistics(long totalUsers, long activeUsers, long inactiveUsers, long lockedUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.lockedUsers = lockedUsers;
        }
        
        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getInactiveUsers() { return inactiveUsers; }
        public long getLockedUsers() { return lockedUsers; }
        
        @Override
        public String toString() {
            return "UserStatistics{" +
                    "totalUsers=" + totalUsers +
                    ", activeUsers=" + activeUsers +
                    ", inactiveUsers=" + inactiveUsers +
                    ", lockedUsers=" + lockedUsers +
                    '}';
        }
    }
} 