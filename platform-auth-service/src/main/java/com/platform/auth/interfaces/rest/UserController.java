package com.platform.auth.interfaces.rest;

import com.platform.auth.application.service.UserApplicationService;
import com.platform.auth.interfaces.dto.user.*;
import com.platform.common.model.PageRequest;
import com.platform.common.model.PageResult;
import com.platform.common.model.ResponseResult;
import com.platform.common.model.ResultCode;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 用户控制器
 * <p>
 * 提供用户管理相关接口
 * </p>
 */
@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    
    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }
    
    /**
     * 创建用户
     *
     * @param createUserRequest 创建用户请求
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseResult<UserDTO> createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequest) {
        UserDTO user = userApplicationService.createUser(createUserRequest);
        return ResponseResult.success(user);
    }
    
    /**
     * 更新用户
     *
     * @param userId 用户ID
     * @param updateUserRequest 更新用户请求
     * @return 更新结果
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseResult<UserDTO> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequestDTO updateUserRequest) {
        UserDTO user = userApplicationService.updateUser(userId, updateUserRequest);
        return ResponseResult.success(user);
    }
    
    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseResult<Void> deleteUser(@PathVariable String userId) {
        userApplicationService.deleteUser(userId);
        return ResponseResult.success();
    }
    
    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseResult<UserDTO> getUser(@PathVariable String userId) {
        UserDTO user = userApplicationService.getUser(userId);
        return ResponseResult.success(user);
    }
    
    /**
     * 分页查询用户
     *
     * @param pageRequest 分页请求
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseResult<PageResult<UserDTO>> getUsers(PageRequest pageRequest) {
        PageResult<UserDTO> users = userApplicationService.getUsers(pageRequest);
        return ResponseResult.success(users);
    }
    
    /**
     * 根据租户ID查询用户
     *
     * @param tenantId 租户ID
     * @return 用户列表
     */
    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseResult<List<UserDTO>> getUsersByTenant(@PathVariable String tenantId) {
        List<UserDTO> users = userApplicationService.getUsersByTenant(tenantId);
        return ResponseResult.success(users);
    }
    
    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param changePasswordRequest 修改密码请求
     * @return 修改结果
     */
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasAuthority('user:update') or #userId == principal.userId")
    public ResponseResult<Void> changePassword(
            @PathVariable String userId,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequest) {
        boolean success = userApplicationService.changePassword(
                userId, 
                changePasswordRequest.getOldPassword(), 
                changePasswordRequest.getNewPassword());
                
        if (success) {
            return ResponseResult.success();
        } else {
            return ResponseResult.failure(ResultCode.PARAM_ERROR, "旧密码错误");
        }
    }
    
    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 重置结果
     */
    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseResult<String> resetPassword(@PathVariable String userId) {
        String newPassword = userApplicationService.resetPassword(userId);
        return ResponseResult.success(newPassword);
    }
    
    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/enable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseResult<Void> enableUser(@PathVariable String userId) {
        userApplicationService.enableUser(userId);
        return ResponseResult.success();
    }
    
    /**
     * 禁用用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/disable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseResult<Void> disableUser(@PathVariable String userId) {
        userApplicationService.disableUser(userId);
        return ResponseResult.success();
    }
    
    /**
     * 分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 操作结果
     */
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseResult<Void> assignRoles(
            @PathVariable String userId,
            @RequestBody Set<String> roleIds) {
        userApplicationService.assignRoles(userId, roleIds);
        return ResponseResult.success();
    }
    
    /**
     * 移除角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 操作结果
     */
    @DeleteMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseResult<Void> removeRoles(
            @PathVariable String userId,
            @RequestBody Set<String> roleIds) {
        userApplicationService.removeRoles(userId, roleIds);
        return ResponseResult.success();
    }
    
    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/current")
    public ResponseResult<UserDTO> getCurrentUser() {
        UserDTO user = userApplicationService.getCurrentUser();
        return ResponseResult.success(user);
    }
}
