package com.platform.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 租户领域模型
 * <p>
 * 作为核心域模型，不包含框架注解，保持领域模型的纯粹性
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    
    /**
     * 租户ID
     */
    private String id;
    
    /**
     * 租户编码（唯一标识）
     */
    private String code;
    
    /**
     * 租户名称
     */
    private String name;
    
    /**
     * 租户状态（0:禁用, 1:启用, -1:已删除）
     */
    private Integer status;
    
    /**
     * 联系人
     */
    private String contactPerson;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    private String contactEmail;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 最大用户数
     */
    private Integer maxUserCount;
    
    /**
     * 当前用户数
     */
    private Integer currentUserCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否已过期
     */
    public boolean isExpired() {
        if (expireTime == null) {
            return false;
        }
        return expireTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * 是否有效
     */
    public boolean isEnabled() {
        return status != null && status == 1 && !isExpired();
    }
    
    /**
     * 是否可以添加用户
     */
    public boolean canAddUser() {
        if (maxUserCount == null || maxUserCount <= 0) {
            return true;
        }
        if (currentUserCount == null) {
            return true;
        }
        return currentUserCount < maxUserCount;
    }
}
