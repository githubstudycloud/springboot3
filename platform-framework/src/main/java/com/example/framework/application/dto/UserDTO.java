package com.example.framework.application.dto;

import java.io.Serializable;

/**
 * 用户DTO
 * 应用层数据传输对象示例
 *
 * @author platform
 * @since 1.0.0
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 无参构造函数
     */
    public UserDTO() {
    }
    
    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * 获取电子邮件
     *
     * @return 电子邮件
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * 设置电子邮件
     *
     * @param email 电子邮件
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    public String getRoleName() {
        return roleName;
    }
    
    /**
     * 设置角色名称
     *
     * @param roleName 角色名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}