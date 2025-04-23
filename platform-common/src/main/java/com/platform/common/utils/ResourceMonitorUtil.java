package com.platform.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility class for monitoring system resources like CPU and memory.
 * Used for self-monitoring to prevent overloading.
 */
@Slf4j
public class ResourceMonitorUtil {

    private static final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    
    // Thresholds for resource usage
    private static final double CPU_THRESHOLD = 0.80; // 80%
    private static final double MEMORY_THRESHOLD = 0.85; // 85%
    
    // Flag to indicate if system is overloaded
    private static final AtomicBoolean systemOverloaded = new AtomicBoolean(false);
    
    private ResourceMonitorUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Check if the system resources are overloaded.
     * This is used to implement self-protection mechanisms.
     *
     * @return true if the system is overloaded, false otherwise
     */
    public static boolean isSystemOverloaded() {
        boolean currentState = systemOverloaded.get();
        
        // Check current CPU and memory usage
        double cpuLoad = getCpuUsage();
        double memoryUsage = getMemoryUsage();
        
        // Update system overloaded state
        if (currentState) {
            // If currently overloaded, only release if both are below thresholds with margin
            if (cpuLoad < (CPU_THRESHOLD - 0.1) && memoryUsage < (MEMORY_THRESHOLD - 0.1)) {
                log.info("System resources returning to normal. CPU: {}%, Memory: {}%", 
                        String.format("%.2f", cpuLoad * 100), 
                        String.format("%.2f", memoryUsage * 100));
                systemOverloaded.set(false);
            }
        } else {
            // If not currently overloaded, set overloaded if either crosses threshold
            if (cpuLoad > CPU_THRESHOLD || memoryUsage > MEMORY_THRESHOLD) {
                log.warn("System resources overloaded! CPU: {}%, Memory: {}%", 
                        String.format("%.2f", cpuLoad * 100), 
                        String.format("%.2f", memoryUsage * 100));
                systemOverloaded.set(true);
            }
        }
        
        return systemOverloaded.get();
    }
    
    /**
     * Get the current CPU usage as a ratio (0.0 to 1.0).
     *
     * @return CPU usage ratio
     */
    public static double getCpuUsage() {
        return operatingSystemMXBean.getSystemLoadAverage() / operatingSystemMXBean.getAvailableProcessors();
    }
    
    /**
     * Get the current memory usage as a ratio (0.0 to 1.0).
     *
     * @return Memory usage ratio
     */
    public static double getMemoryUsage() {
        long usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed();
        long maxMemory = memoryMXBean.getHeapMemoryUsage().getMax() + memoryMXBean.getNonHeapMemoryUsage().getMax();
        
        return (double) usedMemory / maxMemory;
    }
    
    /**
     * Get detailed resource usage information.
     *
     * @return String containing detailed resource usage information
     */
    public static String getResourceUsageInfo() {
        double cpuLoad = getCpuUsage();
        double memoryUsage = getMemoryUsage();
        
        return String.format("CPU Usage: %.2f%%, Memory Usage: %.2f%%, System Overloaded: %s",
                cpuLoad * 100, memoryUsage * 100, systemOverloaded.get());
    }
}
