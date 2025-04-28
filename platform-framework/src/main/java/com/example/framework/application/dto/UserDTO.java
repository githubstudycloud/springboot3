package com.example.framework.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户DTO
 * 应用层数据传输对象示例
 *
 * @author platform
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
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
}