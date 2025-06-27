package com.example.dbmonitor.service;

import com.example.dbmonitor.entity.AlertMessage;
import com.example.dbmonitor.entity.MonitorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemHealthService {
    
    private final AlertService alertService;
    
    /**
     * 检查系统整体健康状况
     */
    public Map<String, Object> checkSystemHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        List<String> issues = new ArrayList<>();
        
        try {
            // 检查JVM健康
            healthInfo.put("jvm", checkJvmHealth(issues));
            
            // 检查系统资源
            healthInfo.put("system", checkSystemResources(issues));
            
            // 检查磁盘空间
            healthInfo.put("disk", checkDiskSpace(issues));
            
            // 检查网络连通性
            healthInfo.put("network", checkNetworkConnectivity(issues));
            
            // 检查运行时间
            healthInfo.put("uptime", checkUptime());
            
            // 设置总体健康状态
            healthInfo.put("healthy", issues.isEmpty());
            healthInfo.put("issues", issues);
            healthInfo.put("timestamp", LocalDateTime.now());
            
            // 如果发现严重问题，发送警报
            if (!issues.isEmpty()) {
                sendSystemHealthAlert(issues);
            }
            
        } catch (SecurityException e) {
            log.error("检查系统健康状况时发生安全权限错误", e);
            issues.add("系统健康检查失败(权限不足): " + e.getMessage());
            healthInfo.put("healthy", false);
            healthInfo.put("issues", issues);
        } catch (OutOfMemoryError e) {
            log.error("检查系统健康状况时发生内存不足错误", e);
            issues.add("系统健康检查失败(内存不足): " + e.getMessage());
            healthInfo.put("healthy", false);
            healthInfo.put("issues", issues);
        }
        
        return healthInfo;
    }
    
    /**
     * 检查JVM健康状况
     */
    private Map<String, Object> checkJvmHealth(List<String> issues) {
        Map<String, Object> jvmInfo = new HashMap<>();
        
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            
            // 堆内存信息
            long usedHeap = memoryBean.getHeapMemoryUsage().getUsed();
            long maxHeap = memoryBean.getHeapMemoryUsage().getMax();
            double heapUsagePercent = (double) usedHeap / maxHeap * 100;
            
            jvmInfo.put("heapUsed", usedHeap);
            jvmInfo.put("heapMax", maxHeap);
            jvmInfo.put("heapUsagePercent", Math.round(heapUsagePercent * 100.0) / 100.0);
            
            // 非堆内存信息
            long usedNonHeap = memoryBean.getNonHeapMemoryUsage().getUsed();
            long maxNonHeap = memoryBean.getNonHeapMemoryUsage().getMax();
            
            jvmInfo.put("nonHeapUsed", usedNonHeap);
            jvmInfo.put("nonHeapMax", maxNonHeap);
            
            // 检查内存使用率
            if (heapUsagePercent > 85) {
                issues.add(String.format("JVM堆内存使用率过高: %.2f%%", heapUsagePercent));
            }
            
            // 线程信息
            int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
            jvmInfo.put("threadCount", threadCount);
            
            if (threadCount > 500) {
                issues.add(String.format("线程数过多: %d", threadCount));
            }
            
        } catch (SecurityException e) {
            log.error("检查JVM健康状况时发生安全权限错误", e);
            issues.add("JVM状态检查失败(权限不足): " + e.getMessage());
        } catch (UnsupportedOperationException e) {
            log.error("检查JVM健康状况时发现不支持的操作", e);
            issues.add("JVM状态检查失败(操作不支持): " + e.getMessage());
        }
        
        return jvmInfo;
    }
    
    /**
     * 检查系统资源
     */
    private Map<String, Object> checkSystemResources(List<String> issues) {
        Map<String, Object> systemInfo = new HashMap<>();
        
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            // 系统负载
            double systemLoad = osBean.getSystemLoadAverage();
            systemInfo.put("systemLoad", systemLoad);
            
            // 可用处理器数
            int availableProcessors = osBean.getAvailableProcessors();
            systemInfo.put("availableProcessors", availableProcessors);
            
            // 检查系统负载
            if (systemLoad > availableProcessors * 0.8) {
                issues.add(String.format("系统负载过高: %.2f (处理器数: %d)", systemLoad, availableProcessors));
            }
            
            // 尝试获取系统特定信息
            if (osBean instanceof com.sun.management.OperatingSystemMXBean unixBean) {
                // 物理内存
                long totalPhysicalMemory = unixBean.getTotalPhysicalMemorySize();
                long freePhysicalMemory = unixBean.getFreePhysicalMemorySize();
                double memoryUsagePercent = (double) (totalPhysicalMemory - freePhysicalMemory) / totalPhysicalMemory * 100;
                
                systemInfo.put("totalPhysicalMemory", totalPhysicalMemory);
                systemInfo.put("freePhysicalMemory", freePhysicalMemory);
                systemInfo.put("memoryUsagePercent", Math.round(memoryUsagePercent * 100.0) / 100.0);
                
                if (memoryUsagePercent > 90) {
                    issues.add(String.format("系统内存使用率过高: %.2f%%", memoryUsagePercent));
                }
                
                // CPU使用率
                double processCpuLoad = unixBean.getProcessCpuLoad() * 100;
                double systemCpuLoad = unixBean.getSystemCpuLoad() * 100;
                
                systemInfo.put("processCpuLoad", Math.round(processCpuLoad * 100.0) / 100.0);
                systemInfo.put("systemCpuLoad", Math.round(systemCpuLoad * 100.0) / 100.0);
                
                if (processCpuLoad > 80) {
                    issues.add(String.format("进程CPU使用率过高: %.2f%%", processCpuLoad));
                }
            }
            
        } catch (SecurityException e) {
            log.error("检查系统资源时发生安全权限错误", e);
            issues.add("系统资源检查失败(权限不足): " + e.getMessage());
        } catch (UnsupportedOperationException e) {
            log.error("检查系统资源时发现不支持的操作", e);
            issues.add("系统资源检查失败(操作不支持): " + e.getMessage());
        }
        
        return systemInfo;
    }
    
    /**
     * 检查磁盘空间
     */
    private Map<String, Object> checkDiskSpace(List<String> issues) {
        Map<String, Object> diskInfo = new HashMap<>();
        List<Map<String, Object>> diskSpaces = new ArrayList<>();
        
        try {
            for (FileStore store : FileSystems.getDefault().getFileStores()) {
                if (store.isReadOnly()) {
                    continue;
                }
                
                Map<String, Object> diskSpace = new HashMap<>();
                
                long totalSpace = store.getTotalSpace();
                long usableSpace = store.getUsableSpace();
                long usedSpace = totalSpace - usableSpace;
                double usagePercent = (double) usedSpace / totalSpace * 100;
                
                diskSpace.put("name", store.name());
                diskSpace.put("type", store.type());
                diskSpace.put("totalSpace", totalSpace);
                diskSpace.put("usableSpace", usableSpace);
                diskSpace.put("usedSpace", usedSpace);
                diskSpace.put("usagePercent", Math.round(usagePercent * 100.0) / 100.0);
                
                diskSpaces.add(diskSpace);
                
                // 检查磁盘使用率
                if (usagePercent > 85) {
                    issues.add(String.format("磁盘 %s 使用率过高: %.2f%%", store.name(), usagePercent));
                }
                
                // 检查可用空间（小于1GB警告）
                if (usableSpace < 1024L * 1024 * 1024) {
                    issues.add(String.format("磁盘 %s 可用空间不足: %.2f MB", 
                        store.name(), usableSpace / 1024.0 / 1024.0));
                }
            }
            
            diskInfo.put("diskSpaces", diskSpaces);
            
        } catch (IOException e) {
            log.error("检查磁盘空间时发生IO错误", e);
            issues.add("磁盘空间检查失败(IO错误): " + e.getMessage());
        } catch (SecurityException e) {
            log.error("检查磁盘空间时发生安全权限错误", e);
            issues.add("磁盘空间检查失败(权限不足): " + e.getMessage());
        }
        
        return diskInfo;
    }
    
    /**
     * 检查网络连通性
     */
    private Map<String, Object> checkNetworkConnectivity(List<String> issues) {
        Map<String, Object> networkInfo = new HashMap<>();
        
        try {
            // 检查本地主机名解析
            String hostName = InetAddress.getLocalHost().getHostName();
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            
            networkInfo.put("hostName", hostName);
            networkInfo.put("hostAddress", hostAddress);
            
            // 检查DNS解析（测试知名网站）
            List<Map<String, Object>> connectivityTests = new ArrayList<>();
            
            String[] testHosts = {"localhost", "127.0.0.1"};
            
            for (String host : testHosts) {
                Map<String, Object> test = new HashMap<>();
                test.put("host", host);
                
                try {
                    long startTime = System.currentTimeMillis();
                    boolean reachable = InetAddress.getByName(host).isReachable(5000);
                    long responseTime = System.currentTimeMillis() - startTime;
                    
                    test.put("reachable", reachable);
                    test.put("responseTime", responseTime);
                    
                    if (!reachable) {
                        issues.add(String.format("无法连接到 %s", host));
                    } else if (responseTime > 2000) {
                        issues.add(String.format("连接 %s 响应缓慢: %dms", host, responseTime));
                    }
                    
                } catch (UnknownHostException e) {
                    test.put("reachable", false);
                    test.put("error", "主机名解析失败: " + e.getMessage());
                    issues.add(String.format("无法解析主机名: %s", host));
                } catch (IOException e) {
                    test.put("reachable", false);
                    test.put("error", "网络IO错误: " + e.getMessage());
                    issues.add(String.format("网络连接错误: %s", host));
                }
                
                connectivityTests.add(test);
            }
            
            networkInfo.put("connectivityTests", connectivityTests);
            
        } catch (UnknownHostException e) {
            log.error("检查网络连通性时发生主机名解析错误", e);
            issues.add("网络连通性检查失败(主机名解析): " + e.getMessage());
        } catch (SecurityException e) {
            log.error("检查网络连通性时发生安全权限错误", e);
            issues.add("网络连通性检查失败(权限不足): " + e.getMessage());
        }
        
        return networkInfo;
    }
    
    /**
     * 检查应用运行时间
     */
    private Map<String, Object> checkUptime() {
        Map<String, Object> uptimeInfo = new HashMap<>();
        
        try {
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            long uptimeMs = runtimeBean.getUptime();
            
            long days = TimeUnit.MILLISECONDS.toDays(uptimeMs);
            long hours = TimeUnit.MILLISECONDS.toHours(uptimeMs) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMs) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMs) % 60;
            
            uptimeInfo.put("uptimeMs", uptimeMs);
            uptimeInfo.put("uptimeFormatted", String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, seconds));
            uptimeInfo.put("startTime", new Date(runtimeBean.getStartTime()));
            
        } catch (Exception e) {
            log.error("检查应用运行时间时发生错误", e);
            uptimeInfo.put("error", e.getMessage());
        }
        
        return uptimeInfo;
    }
    
    /**
     * 发送系统健康警报
     */
    private void sendSystemHealthAlert(List<String> issues) {
        try {
            MonitorResult systemResult = MonitorResult.builder()
                    .dataSourceName("SYSTEM_HEALTH")
                    .timestamp(LocalDateTime.now())
                    .healthy(false)
                    .issues(issues)
                    .longRunningQueries(new ArrayList<>())
                    .metrics(new HashMap<>())
                    .checkDuration(0L)
                    .build();
            
            alertService.sendAlert(systemResult);
            
        } catch (Exception e) {
            log.error("发送系统健康警报失败", e);
        }
    }
}