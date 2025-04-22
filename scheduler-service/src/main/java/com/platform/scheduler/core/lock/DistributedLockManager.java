package com.platform.scheduler.core.lock;

/**
 * 分布式锁管理器接口
 * 
 * @author platform
 */
public interface DistributedLockManager {
    
    /**
     * 尝试获取分布式锁
     * 
     * @param lockKey 锁键
     * @param timeoutMillis 超时时间（毫秒）
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long timeoutMillis);
    
    /**
     * 释放分布式锁
     * 
     * @param lockKey 锁键
     */
    void unlock(String lockKey);
    
    /**
     * 获取锁并执行操作
     * 
     * @param <T> 返回值类型
     * @param lockKey 锁键
     * @param timeoutMillis 超时时间（毫秒）
     * @param action 执行操作
     * @return 操作返回值
     * @throws Exception 执行异常
     */
    <T> T executeWithLock(String lockKey, long timeoutMillis, LockAction<T> action) throws Exception;
    
    /**
     * 锁操作接口
     * 
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    interface LockAction<T> {
        /**
         * 执行操作
         * 
         * @return 操作返回值
         * @throws Exception 执行异常
         */
        T execute() throws Exception;
    }
}
