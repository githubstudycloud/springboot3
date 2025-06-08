package com.platform.domain.user.entity;

import com.platform.domain.user.valueobject.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户实体单元测试
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@ActiveProfiles("test")
class UserTest {
    
    @Test
    @DisplayName("创建用户 - 成功")
    void createUser_Success() {
        // Given
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        
        // When
        User user = new User(username, password, email);
        
        // Then
        assertNotNull(user.getUserId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertNotEquals(password, user.getPassword()); // 密码应该被加密
    }
    
    @Test
    @DisplayName("验证密码 - 正确密码")
    void validatePassword_CorrectPassword() {
        // Given
        String password = "password123";
        User user = new User("testuser", password, "test@example.com");
        
        // When & Then
        assertTrue(user.validatePassword(password));
    }
    
    @Test
    @DisplayName("验证密码 - 错误密码")
    void validatePassword_WrongPassword() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        
        // When & Then
        assertFalse(user.validatePassword("wrongpassword"));
    }
    
    @Test
    @DisplayName("用户登录 - 激活状态")
    void login_ActiveUser() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        
        // When
        assertDoesNotThrow(user::login);
        
        // Then
        assertNotNull(user.getLastLoginAt());
    }
    
    @Test
    @DisplayName("用户登录 - 锁定状态")
    void login_LockedUser() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        user.lock();
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class, 
            user::login
        );
        assertTrue(exception.getMessage().contains("用户状态不允许登录"));
    }
    
    @Test
    @DisplayName("更改密码 - 成功")
    void changePassword_Success() {
        // Given
        String oldPassword = "oldpassword";
        String newPassword = "newpassword";
        User user = new User("testuser", oldPassword, "test@example.com");
        
        // When
        assertDoesNotThrow(() -> user.changePassword(oldPassword, newPassword));
        
        // Then
        assertTrue(user.validatePassword(newPassword));
        assertFalse(user.validatePassword(oldPassword));
    }
    
    @Test
    @DisplayName("更改密码 - 原密码错误")
    void changePassword_WrongOldPassword() {
        // Given
        User user = new User("testuser", "oldpassword", "test@example.com");
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> user.changePassword("wrongpassword", "newpassword")
        );
        assertEquals("原密码不正确", exception.getMessage());
    }
    
    @Test
    @DisplayName("更新用户资料")
    void updateProfile() {
        // Given
        User user = new User("testuser", "password123", "old@example.com");
        String newEmail = "new@example.com";
        String phoneNumber = "1234567890";
        String realName = "Test User";
        
        // When
        user.updateProfile(newEmail, phoneNumber, realName);
        
        // Then
        assertEquals(newEmail, user.getEmail());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(realName, user.getRealName());
    }
    
    @Test
    @DisplayName("激活用户")
    void activateUser() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        user.deactivate();
        
        // When
        user.activate();
        
        // Then
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }
    
    @Test
    @DisplayName("停用用户")
    void deactivateUser() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        
        // When
        user.deactivate();
        
        // Then
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }
    
    @Test
    @DisplayName("锁定用户")
    void lockUser() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        
        // When
        user.lock();
        
        // Then
        assertEquals(UserStatus.LOCKED, user.getStatus());
    }
    
    @Test
    @DisplayName("解锁用户")
    void unlockUser() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        user.lock();
        
        // When
        user.unlock();
        
        // Then
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }
    
    @Test
    @DisplayName("检查用户是否可以执行操作")
    void canPerformOperation() {
        // Given
        User user = new User("testuser", "password123", "test@example.com");
        
        // When & Then
        assertTrue(user.canPerformOperation()); // 激活状态
        
        user.deactivate();
        assertFalse(user.canPerformOperation()); // 停用状态
        
        user.lock();
        assertFalse(user.canPerformOperation()); // 锁定状态
    }
    
    @Test
    @DisplayName("用户相等性测试")
    void userEquality() {
        // Given
        User user1 = new User("testuser1", "password123", "test1@example.com");
        User user2 = new User("testuser2", "password123", "test2@example.com");
        
        // When & Then
        assertNotEquals(user1, user2); // 不同的用户ID
        assertEquals(user1, user1); // 同一个对象
        assertNotEquals(user1, null); // 与null比较
    }
} 