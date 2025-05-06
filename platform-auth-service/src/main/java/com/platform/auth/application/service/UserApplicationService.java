package com.platform.auth.application.service;

import com.platform.auth.domain.model.Role;
import com.platform.auth.domain.model.User;
import com.platform.auth.domain.repository.RoleRepository;
import com.platform.auth.domain.service.UserDomainService;
import com.platform.auth.interfaces.dto.user.*;
import com.platform.common.model.PageRequest;
import com.platform.common.model.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 * <p>
 * 处理用户管理相关的应用逻辑，协调领域服务的调用
 * </p>
 */
@Service
public class UserApplicationService {
    
    private static final Logger log = LoggerFactory.getLogger(UserApplicationService.class);
    
    private final UserDomainService userDomainService;
    private final RoleRepository roleRepository;
    
    public UserApplicationService(
            UserDomainService userDomainService,
            RoleRepository roleRepository) {
        this.userDomainService = userDomainService;
        this.roleRepository = roleRepository;
    }
    
    /**
     * 创建用户
     *
     * @param createUserRequest 创建用户请求
     * @return 用户DTO
     */
    @Transactional
    public UserDTO createUser(CreateUserRequestDTO createUserRequest) {
        // 转换为领域模型
        User user = User.builder()
                .username(createUserRequest.getUsername())
                .password(createUserRequest.getPassword())
                .email(createUserRequest.getEmail())
                .mobile(createUserRequest.getMobile())
                .nickname(createUserRequest.getNickname())
                .avatar(createUserRequest.getAvatar())
                .status(createUserRequest.getStatus())
                .tenantId(createUserRequest.getTenantId())
                .roles(new HashSet<>())
                .build();
        
        // 创建用户
        User createdUser = userDomainService.createUser(user);
        
        // 分配角色
        if (createUserRequest.getRoleIds() != null && !createUserRequest.getRoleIds().isEmpty()) {
            userDomainService.assignRoles(createdUser.getId(), createUserRequest.getRoleIds());
            // 重新获取用户信息（包含角色）
            createdUser = userDomainService.findUser(createdUser.getId()).orElse(createdUser);
        }
        
        // 转换为DTO
        return toUserDTO(createdUser);
    }
    
    /**
     * 更新用户
     *
     * @param userId 用户ID
     * @param updateUserRequest 更新用户请求
     * @return 用户DTO
     */
    @Transactional
    public UserDTO updateUser(String userId, UpdateUserRequestDTO updateUserRequest) {
        // 获取用户
        User user = userDomainService.findUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 更新用户信息
        user.setEmail(updateUserRequest.getEmail());
        user.setMobile(updateUserRequest.getMobile());
        user.setNickname(updateUserRequest.getNickname());
        user.setAvatar(updateUserRequest.getAvatar());
        user.setStatus(updateUserRequest.getStatus());
        user.setUpdateTime(LocalDateTime.now());
        
        // 更新用户
        User updatedUser = userDomainService.updateUser(user);
        
        // 转换为DTO
        return toUserDTO(updatedUser);
    }
    
    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    @Transactional
    public void deleteUser(String userId) {
        userDomainService.deleteUser(userId);
    }
    
    /**
     * 获取用户
     *
     * @param userId 用户ID
     * @return 用户DTO
     */
    public UserDTO getUser(String userId) {
        User user = userDomainService.findUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
                
        return toUserDTO(user);
    }
    
    /**
     * 分页查询用户
     *
     * @param pageRequest 分页请求
     * @return 用户DTO分页结果
     */
    public PageResult<UserDTO> getUsers(PageRequest pageRequest) {
        // 分页查询
        List<User> users = userDomainService.findUsers(
                pageRequest.getPageNum(), 
                pageRequest.getPageSize());
                
        // 获取总数
        long total = userDomainService.count();
        
        // 转换为DTO
        List<UserDTO> userDTOs = users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
                
        // 构建分页结果
        return PageResult.of(userDTOs, total, pageRequest);
    }
    
    /**
     * 根据租户ID查询用户
     *
     * @param tenantId 租户ID
     * @return 用户DTO列表
     */
    public List<UserDTO> getUsersByTenant(String tenantId) {
        List<User> users = userDomainService.findUsersByTenant(tenantId);
        
        return users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Transactional
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        return userDomainService.changePassword(userId, oldPassword, newPassword);
    }
    
    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 新密码
     */
    @Transactional
    public String resetPassword(String userId) {
        return userDomainService.resetPassword(userId);
    }
    
    /**
     * 启用用户
     *
     * @param userId 用户ID
     */
    @Transactional
    public void enableUser(String userId) {
        userDomainService.enableUser(userId);
    }
    
    /**
     * 禁用用户
     *
     * @param userId 用户ID
     */
    @Transactional
    public void disableUser(String userId) {
        userDomainService.disableUser(userId);
    }
    
    /**
     * 分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     */
    @Transactional
    public void assignRoles(String userId, Set<String> roleIds) {
        userDomainService.assignRoles(userId, roleIds);
    }
    
    /**
     * 移除角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     */
    @Transactional
    public void removeRoles(String userId, Set<String> roleIds) {
        userDomainService.removeRoles(userId, roleIds);
    }
    
    /**
     * 获取当前用户信息
     *
     * @return 用户DTO
     */
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userDomainService.findUserByUsername(username)
                .orElseThrow(() -> new IllegalStateException("当前用户不存在"));
                
        return toUserDTO(user);
    }
    
    /**
     * 将用户领域模型转换为DTO
     *
     * @param user 用户领域模型
     * @return 用户DTO
     */
    private UserDTO toUserDTO(User user) {
        // 提取角色ID和名称
        Set<String> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
                
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        
        // 构建DTO
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .tenantId(user.getTenantId())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .roleIds(roleIds)
                .roleNames(roleNames)
                .build();
    }
}
