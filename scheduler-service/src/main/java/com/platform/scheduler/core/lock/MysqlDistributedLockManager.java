package com.platform.scheduler.core.lock;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 基于MySQL的分布式锁管理器实现
 * 作为Redis锁的备用方案，优先使用Redis实现
 * 
 * @author platform
 */
@Component
public class MysqlDistributedLockManager implements DistributedLockManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MysqlDistributedLockManager.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 当前节点ID
     */
    private String nodeId = UUID.randomUUID().toString();
    
    /**
     * 线程本地存储锁值
     */
    private final ThreadLocal<String> lockValueHolder = new ThreadLocal<>();
    
    @Override
    public boolean tryLock(String lockKey, long timeoutMillis) {
        String lockValue = UUID.randomUUID().toString();
        Date expireTime = new Date(System.currentTimeMillis() + timeoutMillis);
        Date now = new Date();
        
        try {
            // 1. 清理过期锁
            jdbcTemplate.update(
                "DELETE FROM distributed_lock WHERE expire_time < ?",
                now
            );
            
            // 2. 尝试插入锁记录
            jdbcTemplate.update(
                "INSERT INTO distributed_lock (lock_key, lock_value, node_id, expire_time, created_time) VALUES (?, ?, ?, ?, ?)",
                lockKey, lockValue, nodeId, expireTime, now
            );
            
            // 成功获取锁
            lockValueHolder.set(lockValue);
            return true;
        } catch (DuplicateKeyException e) {
            // 锁已存在
            logger.debug("获取MySQL分布式锁失败，锁已存在: {}", lockKey);
            return false;
        } catch (Exception e) {
            logger.error("获取MySQL分布式锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    @Override
    public void unlock(String lockKey) {
        String lockValue = lockValueHolder.get();
        if (lockValue != null) {
            try {
                // 只删除自己持有的锁
                jdbcTemplate.update(
                    "DELETE FROM distributed_lock WHERE lock_key = ? AND lock_value = ?",
                    lockKey, lockValue
                );
                
                lockValueHolder.remove();
            } catch (Exception e) {
                logger.error("释放MySQL分布式锁异常: {}", lockKey, e);
            }
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
                throw new RuntimeException("获取MySQL分布式锁失败: " + lockKey);
            }
        } finally {
            if (locked) {
                unlock(lockKey);
            }
        }
    }
}
