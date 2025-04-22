package com.platform.scheduler.core.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Redisson的分布式锁管理器实现
 * 
 * @author platform
 */
@Component
public class RedisDistributedLockManager implements DistributedLockManager {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLockManager.class);
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Override
    public boolean tryLock(String lockKey, long timeoutMillis) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(0, timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("获取分布式锁被中断: {}", lockKey, e);
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            logger.error("获取分布式锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Exception e) {
            logger.error("释放分布式锁异常: {}", lockKey, e);
        }
    }
    
    @Override
    public <T> T executeWithLock(String lockKey, long timeoutMillis, LockAction<T> action) throws Exception {
        boolean locked = false;
        try {
            locked = tryLock(lockKey, timeoutMillis);
            if (locked) {
                return action.execute();
            } else {
                throw new RuntimeException("获取分布式锁失败: " + lockKey);
            }
        } finally {
            if (locked) {
                unlock(lockKey);
            }
        }
    }
}
