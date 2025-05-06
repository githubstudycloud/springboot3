package com.platform.scheduler.infrastructure.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的分布式锁实现
 * 使用Redisson客户端实现分布式锁
 *
 * @author platform
 */
@Component
public class RedisDistributedLock implements DistributedLock {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);
    
    private final RedissonClient redissonClient;
    
    @Autowired
    public RedisDistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
    
    @Override
    public String tryLock(String lockKey, int timeoutSeconds) {
        return tryLock(lockKey, 0, timeoutSeconds);
    }
    
    @Override
    public String tryLock(String lockKey, int waitTimeSeconds, int timeoutSeconds) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock key cannot be null or empty");
        }
        
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Timeout seconds must be positive");
        }
        
        String lockIdentifier = UUID.randomUUID().toString();
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            boolean locked = lock.tryLock(waitTimeSeconds, timeoutSeconds, TimeUnit.SECONDS);
            if (locked) {
                logger.debug("Successfully acquired lock: {}, identifier: {}", lockKey, lockIdentifier);
                return lockIdentifier;
            } else {
                logger.debug("Failed to acquire lock: {}", lockKey);
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while trying to acquire lock: {}", lockKey, e);
            return null;
        } catch (Exception e) {
            logger.error("Error acquiring lock: {}", lockKey, e);
            return null;
        }
    }
    
    @Override
    public boolean releaseLock(String lockKey, String lockIdentifier) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock key cannot be null or empty");
        }
        
        if (lockIdentifier == null || lockIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock identifier cannot be null or empty");
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.isLocked()) {
                lock.unlock();
                logger.debug("Released lock: {}, identifier: {}", lockKey, lockIdentifier);
                return true;
            } else {
                logger.debug("Lock not found or already released: {}", lockKey);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error releasing lock: {}", lockKey, e);
            return false;
        }
    }
    
    @Override
    public boolean forceReleaseLock(String lockKey) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock key cannot be null or empty");
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.isLocked()) {
                lock.forceUnlock();
                logger.warn("Force released lock: {}", lockKey);
                return true;
            } else {
                logger.debug("Lock not found or already released: {}", lockKey);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error force releasing lock: {}", lockKey, e);
            return false;
        }
    }
    
    @Override
    public boolean refreshLock(String lockKey, String lockIdentifier, int timeoutSeconds) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock key cannot be null or empty");
        }
        
        if (lockIdentifier == null || lockIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock identifier cannot be null or empty");
        }
        
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Timeout seconds must be positive");
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.isLocked()) {
                // 刷新锁的过期时间
                lock.expire(timeoutSeconds, TimeUnit.SECONDS);
                logger.debug("Refreshed lock: {}, identifier: {}, timeout: {} seconds", 
                        lockKey, lockIdentifier, timeoutSeconds);
                return true;
            } else {
                logger.debug("Lock not found or already released: {}", lockKey);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error refreshing lock: {}", lockKey, e);
            return false;
        }
    }
    
    @Override
    public boolean isLocked(String lockKey) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock key cannot be null or empty");
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            return lock.isLocked();
        } catch (Exception e) {
            logger.error("Error checking lock status: {}", lockKey, e);
            return false;
        }
    }
    
    @Override
    public long getLockExpirationTime(String lockKey) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Lock key cannot be null or empty");
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.isLocked()) {
                // 获取锁的剩余过期时间(毫秒)，转换为秒
                return lock.remainTimeToLive() / 1000;
            } else {
                return -1;
            }
        } catch (Exception e) {
            logger.error("Error getting lock expiration time: {}", lockKey, e);
            return -1;
        }
    }
}
