package com.platform.scheduler.exception;

/**
 * 分布式锁异常
 * 
 * @author platform
 */
public class DistributedLockException extends SchedulerException {

    private static final long serialVersionUID = 1L;
    
    /**
     * 锁键
     */
    private String lockKey;

    /**
     * 构造函数
     * 
     * @param lockKey 锁键
     * @param message 错误消息
     */
    public DistributedLockException(String lockKey, String message) {
        super("DISTRIBUTED_LOCK_ERROR", message);
        this.lockKey = lockKey;
    }
    
    /**
     * 构造函数
     * 
     * @param lockKey 锁键
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DistributedLockException(String lockKey, String message, Throwable cause) {
        super("DISTRIBUTED_LOCK_ERROR", message, cause);
        this.lockKey = lockKey;
    }

    /**
     * 获取锁键
     * 
     * @return 锁键
     */
    public String getLockKey() {
        return lockKey;
    }
}
