package com.platform.scheduler.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 调度器线程工厂
 * 
 * @author platform
 */
public class SchedulerThreadFactory implements ThreadFactory {
    
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    
    /**
     * 创建线程工厂
     * 
     * @param namePrefix 线程名前缀
     */
    public SchedulerThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }
    
    /**
     * 创建线程工厂
     * 
     * @param namePrefix 线程名前缀
     * @param daemon 是否为守护线程
     */
    public SchedulerThreadFactory(String namePrefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-thread-";
        this.daemon = daemon;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
