package com.platform.scheduler.infrastructure.lock;

/**
 * 分布式锁接口
 * 定义分布式锁的获取和释放操作
 *
 * @author platform
 */
public interface DistributedLock {
    
    /**
     * 尝试获取锁
     *
     * @param lockKey 锁的键
     * @param timeoutSeconds 获取锁的超时时间(秒)
     * @return 如果成功获取锁，则返回锁的标识符；否则返回null
     */
    String tryLock(String lockKey, int timeoutSeconds);
    
    /**
     * 尝试获取锁
     *
     * @param lockKey 锁的键
     * @param waitTimeSeconds 等待获取锁的时间(秒)
     * @param timeoutSeconds 锁的超时时间(秒)
     * @return 如果成功获取锁，则返回锁的标识符；否则返回null
     */
    String tryLock(String lockKey, int waitTimeSeconds, int timeoutSeconds);
    
    /**
     * 释放锁
     *
     * @param lockKey 锁的键
     * @param lockIdentifier 锁的标识符
     * @return 如果成功释放锁，则返回true；否则返回false
     */
    boolean releaseLock(String lockKey, String lockIdentifier);
    
    /**
     * 强制释放锁
     * 无论锁的标识符是否匹配，都释放锁
     * 注意：此方法谨慎使用，可能会释放其他进程持有的锁
     *
     * @param lockKey 锁的键
     * @return 如果成功释放锁，则返回true；否则返回false
     */
    boolean forceReleaseLock(String lockKey);
    
    /**
     * 刷新锁的过期时间
     *
     * @param lockKey 锁的键
     * @param lockIdentifier 锁的标识符
     * @param timeoutSeconds 新的超时时间(秒)
     * @return 如果成功刷新锁，则返回true；否则返回false
     */
    boolean refreshLock(String lockKey, String lockIdentifier, int timeoutSeconds);
    
    /**
     * 检查锁是否存在
     *
     * @param lockKey 锁的键
     * @return 如果锁存在，则返回true；否则返回false
     */
    boolean isLocked(String lockKey);
    
    /**
     * 获取锁的剩余过期时间
     *
     * @param lockKey 锁的键
     * @return 剩余过期时间(秒)，如果锁不存在，则返回-1
     */
    long getLockExpirationTime(String lockKey);
}
