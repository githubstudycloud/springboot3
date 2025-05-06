package com.platform.auth.domain.service.impl;

import com.platform.auth.domain.model.Role;
import com.platform.auth.domain.model.User;
import com.platform.auth.domain.repository.RoleRepository;
import com.platform.auth.domain.repository.TenantRepository;
import com.platform.auth.domain.repository.UserRepository;
import com.platform.auth.domain.service.AuthenticationDomainService;
import com.platform.auth.domain.service.UserDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户领域服务实现
 * <p>
 * 实现用户管理相关的核心业务逻辑
 * </p>
 */
@Service
public class UserDomainServiceImpl implements UserDomainService {
    
    private static final Logger log = LoggerFactory.getLogger(UserDomainServiceImpl.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final AuthenticationDomainService authenticationDomainService;
    
    public UserDomainServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            TenantRepository tenantRepository,
            AuthenticationDomainService authenticationDomainService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tenantRepository = tenantRepository;
        this.authenticationDomainService = authenticationDomainService;
    }
    
    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 检查电子邮件是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("电子邮件已存在");
        }
        
        // 检查手机号是否已存在
        if (user.getMobile() != null && userRepository.existsByMobile(user.getMobile())) {
            throw new IllegalArgumentException("手机号已存在");
        }
        
        // 检查租户是否存在且可用
        if (user.getTenantId() != null) {
            tenantRepository.findById(user.getTenantId())
                    .filter(tenant -> tenant.isEnabled())
                    .filter(tenant -> tenant.canAddUser())
                    .orElseThrow(() -> new IllegalArgumentException("租户不存在或已达到用户上限"));
                    
            // 增加租户用户数量
            tenantRepository.updateUserCount(user.getTenantId(), 
                    tenantRepository.findById(user.getTenantId()).get().getCurrentUserCount() + 1);
        }
        
        // 设置创建时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        // 设置默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        // 加密密码
        user.setPassword(authenticationDomainService.encryptPassword(user.getPassword()));
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        log.info("创建用户成功: {}", savedUser.getUsername());
        return savedUser;
    }
    
    @Override
    @Transactional
    public User updateUser(User user) {
        // 检查用户是否存在
        userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        // 保存用户
        User updatedUser = userRepository.save(user);
        
        log.info("更新用户成功: {}", updatedUser.getUsername());
        return updatedUser;
    }
    
    @Override
    @Transactional
    public void deleteUser(String userId) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 减少租户用户数量
        if (user.getTenantId() != null) {
            tenantRepository.updateUserCount(user.getTenantId(), 
                    tenantRepository.findById(user.getTenantId()).get().getCurrentUserCount() - 1);
        }
        
        // 删除用户
        userRepository.deleteById(userId);
        
        log.info("删除用户成功: {}", user.getUsername());
    }
    
    @Override
    public Optional<User> findUser(String userId) {
        return userRepository.findById(userId);
    }
    
    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }
    
    @Override
    public List<User> findUsers(int page, int size) {
        return userRepository.findAll(page, size);
    }
    
    @Override
    public List<User> findUsersByTenant(String tenantId) {
        return userRepository.findByTenantId(tenantId);
    }
    
    @Override
    @Transactional
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 验证旧密码
        if (!authenticationDomainService.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新密码
        user.setPassword(authenticationDomainService.encryptPassword(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户密码修改成功: {}", user.getUsername());
        return true;
    }
    
    @Override
    @Transactional
    public String resetPassword(String userId) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 生成随机密码
        String newPassword = generateRandomPassword();
        
        // 更新密码
        user.setPassword(authenticationDomainService.encryptPassword(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户密码重置成功: {}", user.getUsername());
        return newPassword;
    }
    
    @Override
    @Transactional
    public void enableUser(String userId) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 更新状态
        user.setStatus(1);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户启用成功: {}", user.getUsername());
    }
    
    @Override
    @Transactional
    public void disableUser(String userId) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 更新状态
        user.setStatus(0);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户禁用成功: {}", user.getUsername());
    }
    
    @Override
    @Transactional
    public void lockUser(String userId) {
        // 此处实现与禁用用户功能相同
        disableUser(userId);
        log.info("用户锁定成功: {}", userId);
    }
    
    @Override
    @Transactional
    public void unlockUser(String userId) {
        // 此处实现与启用用户功能相同
        enableUser(userId);
        log.info("用户解锁成功: {}", userId);
    }
    
    @Override
    @Transactional
    public void assignRoles(String userId, Set<String> roleIds) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 获取角色列表
        Set<Role> roles = roleRepository.findByIdIn(roleIds);
        
        // 清空并重新设置角色
        user.getRoles().clear();
        for (Role role : roles) {
            user.addRole(role);
        }
        
        // 更新用户
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户角色分配成功: {}, 角色数量: {}", user.getUsername(), roles.size());
    }
    
    @Override
    @Transactional
    public void removeRoles(String userId, Set<String> roleIds) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        // 获取角色列表
        Set<Role> roles = roleRepository.findByIdIn(roleIds);
        
        // 移除指定角色
        for (Role role : roles) {
            user.removeRole(role);
        }
        
        // 更新用户
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户角色移除成功: {}, 移除角色数量: {}", user.getUsername(), roles.size());
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean isMobileExists(String mobile) {
        return userRepository.existsByMobile(mobile);
    }
    
    /**
     * 生成随机密码
     *
     * @return 随机密码
     */
    private String generateRandomPassword() {
        // 生成8位随机密码，包含字母和数字
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
