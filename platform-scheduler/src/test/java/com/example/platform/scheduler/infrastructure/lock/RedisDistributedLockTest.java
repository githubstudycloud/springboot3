package com.example.platform.scheduler.infrastructure.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisDistributedLockTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOps;
    
    private RedisDistributedLock distributedLock;
    
    private static final String LOCK_KEY = "test-lock";
    private static final String LOCK_VALUE = "test-instance-id";
    private static final int LOCK_TIMEOUT = 30; // seconds
    
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        distributedLock = new RedisDistributedLock(redisTemplate, LOCK_VALUE);
    }
    
    @Test
    void tryLock_shouldReturnTrue_whenLockAcquired() {
        // Given
        when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        
        // When
        boolean result = distributedLock.tryLock(LOCK_KEY, LOCK_TIMEOUT);
        
        // Then
        assertTrue(result);
        verify(valueOps).setIfAbsent(
            eq("lock:" + LOCK_KEY), 
            eq(LOCK_VALUE), 
            eq((long) LOCK_TIMEOUT), 
            eq(TimeUnit.SECONDS)
        );
    }
    
    @Test
    void tryLock_shouldReturnFalse_whenLockAlreadyExists() {
        // Given
        when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class))).thenReturn(false);
        
        // When
        boolean result = distributedLock.tryLock(LOCK_KEY, LOCK_TIMEOUT);
        
        // Then
        assertFalse(result);
        verify(valueOps).setIfAbsent(
            eq("lock:" + LOCK_KEY), 
            eq(LOCK_VALUE), 
            eq((long) LOCK_TIMEOUT), 
            eq(TimeUnit.SECONDS)
        );
    }
    
    @Test
    void releaseLock_shouldReturnTrue_whenLockReleased() {
        // Given
        @SuppressWarnings("unchecked")
        RedisScript<Long> redisScript = any(RedisScript.class);
        
        when(redisTemplate.execute(
            any(RedisScript.class),
            eq(Collections.singletonList("lock:" + LOCK_KEY)),
            eq(LOCK_VALUE)
        )).thenReturn(1L);
        
        // When
        boolean result = distributedLock.releaseLock(LOCK_KEY);
        
        // Then
        assertTrue(result);
        verify(redisTemplate).execute(
            any(RedisScript.class),
            eq(Collections.singletonList("lock:" + LOCK_KEY)),
            eq(LOCK_VALUE)
        );
    }
    
    @Test
    void releaseLock_shouldReturnFalse_whenLockNotReleasedOrNotFound() {
        // Given
        @SuppressWarnings("unchecked")
        RedisScript<Long> redisScript = any(RedisScript.class);
        
        when(redisTemplate.execute(
            any(RedisScript.class),
            eq(Collections.singletonList("lock:" + LOCK_KEY)),
            eq(LOCK_VALUE)
        )).thenReturn(0L);
        
        // When
        boolean result = distributedLock.releaseLock(LOCK_KEY);
        
        // Then
        assertFalse(result);
        verify(redisTemplate).execute(
            any(RedisScript.class),
            eq(Collections.singletonList("lock:" + LOCK_KEY)),
            eq(LOCK_VALUE)
        );
    }
    
    @Test
    void extendLock_shouldReturnTrue_whenLockExtended() {
        // Given
        when(valueOps.getAndExpire(eq("lock:" + LOCK_KEY), eq((long) LOCK_TIMEOUT), eq(TimeUnit.SECONDS)))
            .thenReturn(LOCK_VALUE);
        
        // When
        boolean result = distributedLock.extendLock(LOCK_KEY, LOCK_TIMEOUT);
        
        // Then
        assertTrue(result);
        verify(valueOps).getAndExpire(
            eq("lock:" + LOCK_KEY), 
            eq((long) LOCK_TIMEOUT), 
            eq(TimeUnit.SECONDS)
        );
    }
    
    @Test
    void extendLock_shouldReturnFalse_whenLockNotOwnedOrNotFound() {
        // Given
        when(valueOps.getAndExpire(eq("lock:" + LOCK_KEY), eq((long) LOCK_TIMEOUT), eq(TimeUnit.SECONDS)))
            .thenReturn("different-instance-id");
        
        // When
        boolean result = distributedLock.extendLock(LOCK_KEY, LOCK_TIMEOUT);
        
        // Then
        assertFalse(result);
        verify(valueOps).getAndExpire(
            eq("lock:" + LOCK_KEY), 
            eq((long) LOCK_TIMEOUT), 
            eq(TimeUnit.SECONDS)
        );
    }
}