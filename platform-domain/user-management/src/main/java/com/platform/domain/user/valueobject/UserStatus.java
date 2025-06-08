package com.platform.domain.user.valueobject;

/**
 * 用户状态枚举
 * DDD设计模式 - 值对象
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum UserStatus {
    
    /**
     * 激活状态 - 正常使用
     */
    ACTIVE("激活", true),
    
    /**
     * 非激活状态 - 暂时停用
     */
    INACTIVE("非激活", false),
    
    /**
     * 锁定状态 - 禁止登录
     */
    LOCKED("锁定", false),
    
    /**
     * 删除状态 - 软删除
     */
    DELETED("已删除", false);
    
    private final String description;
    private final boolean canLogin;
    
    UserStatus(String description, boolean canLogin) {
        this.description = description;
        this.canLogin = canLogin;
    }
    
    /**
     * 检查是否可以登录
     */
    public boolean canLogin() {
        return canLogin;
    }
    
    /**
     * 检查是否可以执行业务操作
     */
    public boolean canPerformOperation() {
        return this == ACTIVE;
    }
    
    /**
     * 检查是否可以修改信息
     */
    public boolean canModify() {
        return this == ACTIVE || this == INACTIVE;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
} 