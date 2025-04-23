package org.example.platform.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

/**
 * System Resource Monitor
 * For monitoring CPU and memory usage
 */
@Slf4j
public class SystemResourceMonitor {

    private static final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    
    // Threshold for high CPU usage (80%)
    private static final double CPU_THRESHOLD = 0.8;
    
    // Threshold for high memory usage (85%)
    private static final double MEMORY_THRESHOLD = 0.85;

    /**
     * Get current CPU load
     * @return CPU load as a value between 0.0 and 1.0
     */
    public static double getCpuLoad() {
        return osMXBean.getSystemLoadAverage() / osMXBean.getAvailableProcessors();
    }

    /**
     * Get memory usage ratio
     * @return Memory usage as a value between 0.0 and 1.0
     */
    public static double getMemoryUsage() {
        long usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryMXBean.getHeapMemoryUsage().getMax();
        return (double) usedMemory / maxMemory;
    }
    
    /**
     * Check if CPU usage is above threshold
     * @return true if CPU usage is high
     */
    public static boolean isCpuUsageHigh() {
        return getCpuLoad() > CPU_THRESHOLD;
    }
    
    /**
     * Check if memory usage is above threshold
     * @return true if memory usage is high
     */
    public static boolean isMemoryUsageHigh() {
        return getMemoryUsage() > MEMORY_THRESHOLD;
    }
    
    /**
     * Check if system resources are constrained
     * @return true if either CPU or memory usage is high
     */
    public static boolean isSystemConstrained() {
        boolean cpuHigh = isCpuUsageHigh();
        boolean memoryHigh = isMemoryUsageHigh();
        
        if (cpuHigh || memoryHigh) {
            log.warn("System resources constrained: CPU high: {}, Memory high: {}", cpuHigh, memoryHigh);
            return true;
        }
        
        return false;
    }
}
