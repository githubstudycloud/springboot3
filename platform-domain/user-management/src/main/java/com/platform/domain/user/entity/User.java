package com.platform.domain.user.entity;

import com.platform.domain.user.event.UserEvent;
import com.platform.domain.user.valueobject.UserId;
import com.platform.domain.user.valueobject.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户聚合根
 * DDD领域模型 - 封装用户业务逻辑和不变性约束
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "platform_user")
public class User extends AbstractAggregateRoot<User> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private UserId userId;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    @Column(nullable = false)
    private String password;
    
    @Email(message = "邮箱格式不正确")
    @Column(unique = true)
    private String email;
    
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phoneNumber;
    
    @Size(max = 100, message = "真实姓名长度不能超过100个字符")
    private String realName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastLoginAt;
    
    @Version
    private Long version;
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // JPA required
    protected User() {}
    
    /**
     * 创建新用户
     */
    public User(String username, String password, String email) {
        this.userId = UserId.generate();
        this.username = username;
        this.password = encodePassword(password);
        this.email = email;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // 发布领域事件
        registerEvent(UserEvent.userCreated(this.userId, username, email));
    }
    
    /**
     * 用户登录
     */
    public void login() {
        if (!this.status.canLogin()) {
            throw new IllegalStateException("用户状态不允许登录: " + this.status);
        }
        
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(UserEvent.userLoggedIn(this.userId, this.username));
    }
    
    /**
     * 验证密码
     */
    public boolean validatePassword(String rawPassword) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
    
    /**
     * 更改密码
     */
    public void changePassword(String oldPassword, String newPassword) {
        if (!validatePassword(oldPassword)) {
            throw new IllegalArgumentException("原密码不正确");
        }
        
        this.password = encodePassword(newPassword);
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(UserEvent.userPasswordChanged(this.userId, this.username));
    }
    
    /**
     * 更新用户信息
     */
    public void updateProfile(String email, String phoneNumber, String realName) {
        if (email != null && !email.equals(this.email)) {
            this.email = email;
        }
        
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        
        if (realName != null) {
            this.realName = realName;
        }
        
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(UserEvent.userProfileUpdated(this.userId, this.username));
    }
    
    /**
     * 激活用户
     */
    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            return;
        }
        
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(UserEvent.userActivated(this.userId, this.username));
    }
    
    /**
     * 停用用户
     */
    public void deactivate() {
        if (this.status == UserStatus.INACTIVE) {
            return;
        }
        
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(UserEvent.userDeactivated(this.userId, this.username));
    }
    
    /**
     * 锁定用户
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(UserEvent.userLocked(this.userId, this.username));
    }
    
    /**
     * 解锁用户
     */
    public void unlock() {
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
            
            registerEvent(UserEvent.userUnlocked(this.userId, this.username));
        }
    }
    
    /**
     * 检查用户是否可以执行操作
     */
    public boolean canPerformOperation() {
        return this.status == UserStatus.ACTIVE;
    }
    
    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    // Getters
    public Long getId() { return id; }
    public UserId getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRealName() { return realName; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public Long getVersion() { return version; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
} 