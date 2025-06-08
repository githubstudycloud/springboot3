package com.platform.domain.user.event;

import com.platform.domain.user.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户领域事件
 * DDD设计模式 - 领域事件
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public class UserEvent {
    
    private final String eventType;
    private final UserId userId;
    private final String username;
    private final String email;
    private final LocalDateTime occurredOn;
    private final String description;
    
    private UserEvent(String eventType, UserId userId, String username, String email, String description) {
        this.eventType = eventType;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.description = description;
        this.occurredOn = LocalDateTime.now();
    }
    
    /**
     * 用户创建事件
     */
    public static UserEvent userCreated(UserId userId, String username, String email) {
        return new UserEvent("USER_CREATED", userId, username, email, 
                "用户 " + username + " 已创建");
    }
    
    /**
     * 用户登录事件
     */
    public static UserEvent userLoggedIn(UserId userId, String username) {
        return new UserEvent("USER_LOGGED_IN", userId, username, null, 
                "用户 " + username + " 已登录");
    }
    
    /**
     * 用户密码更改事件
     */
    public static UserEvent userPasswordChanged(UserId userId, String username) {
        return new UserEvent("USER_PASSWORD_CHANGED", userId, username, null, 
                "用户 " + username + " 已更改密码");
    }
    
    /**
     * 用户资料更新事件
     */
    public static UserEvent userProfileUpdated(UserId userId, String username) {
        return new UserEvent("USER_PROFILE_UPDATED", userId, username, null, 
                "用户 " + username + " 已更新资料");
    }
    
    /**
     * 用户激活事件
     */
    public static UserEvent userActivated(UserId userId, String username) {
        return new UserEvent("USER_ACTIVATED", userId, username, null, 
                "用户 " + username + " 已激活");
    }
    
    /**
     * 用户停用事件
     */
    public static UserEvent userDeactivated(UserId userId, String username) {
        return new UserEvent("USER_DEACTIVATED", userId, username, null, 
                "用户 " + username + " 已停用");
    }
    
    /**
     * 用户锁定事件
     */
    public static UserEvent userLocked(UserId userId, String username) {
        return new UserEvent("USER_LOCKED", userId, username, null, 
                "用户 " + username + " 已锁定");
    }
    
    /**
     * 用户解锁事件
     */
    public static UserEvent userUnlocked(UserId userId, String username) {
        return new UserEvent("USER_UNLOCKED", userId, username, null, 
                "用户 " + username + " 已解锁");
    }
    
    // Getters
    public String getEventType() { return eventType; }
    public UserId getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public LocalDateTime getOccurredOn() { return occurredOn; }
    public String getDescription() { return description; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEvent userEvent = (UserEvent) o;
        return Objects.equals(eventType, userEvent.eventType) &&
                Objects.equals(userId, userEvent.userId) &&
                Objects.equals(occurredOn, userEvent.occurredOn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventType, userId, occurredOn);
    }
    
    @Override
    public String toString() {
        return "UserEvent{" +
                "eventType='" + eventType + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
} 