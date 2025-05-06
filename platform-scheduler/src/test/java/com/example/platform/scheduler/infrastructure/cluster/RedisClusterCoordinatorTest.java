package com.example.platform.scheduler.infrastructure.cluster;

import com.example.platform.scheduler.domain.executor.ExecutorId;
import com.example.platform.scheduler.domain.executor.NodeInfo;
import com.example.platform.scheduler.domain.job.JobId;
import com.example.platform.scheduler.domain.taskinstance.TaskInstanceId;
import com.example.platform.scheduler.infrastructure.lock.DistributedLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisClusterCoordinatorTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    
    @Mock
    private HashOperations<String, String, String> hashOps;
    
    @Mock
    private SetOperations<String, String> setOps;
    
    @Mock
    private ValueOperations<String, String> valueOps;
    
    @Mock
    private DistributedLock distributedLock;
    
    private RedisClusterCoordinator clusterCoordinator;
    
    private ExecutorId executorId;
    private NodeInfo nodeInfo;
    
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        
        executorId = new ExecutorId(UUID.randomUUID().toString());
        nodeInfo = new NodeInfo("test-host", "127.0.0.1", "v1.0.0");
        
        clusterCoordinator = new RedisClusterCoordinator(redisTemplate, distributedLock);
    }
    
    @Test
    void registerExecutor_shouldAddExecutorToCluster() {
        // Given
        Map<String, String> executorData = new HashMap<>();
        executorData.put("host", nodeInfo.getHost());
        executorData.put("ip", nodeInfo.getIpAddress());
        executorData.put("version", nodeInfo.getVersion());
        executorData.put("status", "REGISTERED");
        executorData.put("registrationTime", LocalDateTime.now().toString());
        
        // When
        clusterCoordinator.registerExecutor(executorId, nodeInfo);
        
        // Then
        verify(hashOps).putAll(eq("executor:" + executorId.getValue()), anyMap());
        verify(setOps).add(eq("executors"), eq(executorId.getValue()));
        verify(redisTemplate).expire(eq("executor:" + executorId.getValue()), anyLong(), eq(TimeUnit.SECONDS));
    }
    
    @Test
    void updateExecutorHeartbeat_shouldUpdateHeartbeatTime() {
        // Given
        String executorKey = "executor:" + executorId.getValue();
        
        // When
        clusterCoordinator.updateExecutorHeartbeat(executorId);
        
        // Then
        verify(hashOps).put(eq(executorKey), eq("lastHeartbeat"), anyString());
        verify(redisTemplate).expire(eq(executorKey), anyLong(), eq(TimeUnit.SECONDS));
    }
    
    @Test
    void getActiveExecutors_shouldReturnOnlyActiveExecutors() {
        // Given
        Set<String> allExecutors = new HashSet<>(Arrays.asList("exec1", "exec2", "exec3"));
        when(setOps.members(eq("executors"))).thenReturn(allExecutors);
        
        Map<String, String> activeExecutorData = new HashMap<>();
        activeExecutorData.put("status", "ONLINE");
        activeExecutorData.put("lastHeartbeat", LocalDateTime.now().toString());
        
        Map<String, String> offlineExecutorData = new HashMap<>();
        offlineExecutorData.put("status", "OFFLINE");
        offlineExecutorData.put("lastHeartbeat", LocalDateTime.now().minusHours(1).toString());
        
        when(hashOps.entries(eq("executor:exec1"))).thenReturn(activeExecutorData);
        when(hashOps.entries(eq("executor:exec2"))).thenReturn(activeExecutorData);
        when(hashOps.entries(eq("executor:exec3"))).thenReturn(offlineExecutorData);
        
        // When
        List<ExecutorId> activeExecutors = clusterCoordinator.getActiveExecutors();
        
        // Then
        assertEquals(2, activeExecutors.size());
        assertTrue(activeExecutors.stream().anyMatch(id -> id.getValue().equals("exec1")));
        assertTrue(activeExecutors.stream().anyMatch(id -> id.getValue().equals("exec2")));
        assertFalse(activeExecutors.stream().anyMatch(id -> id.getValue().equals("exec3")));
    }
    
    @Test
    void lockTaskExecution_shouldReturnTrue_whenLockAcquired() {
        // Given
        TaskInstanceId taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
        when(distributedLock.tryLock(anyString(), anyInt())).thenReturn(true);
        
        // When
        boolean result = clusterCoordinator.lockTaskExecution(taskInstanceId, executorId);
        
        // Then
        assertTrue(result);
        verify(distributedLock).tryLock(eq("task_execution:" + taskInstanceId.getValue()), anyInt());
        verify(hashOps).put(eq("task_locks"), eq(taskInstanceId.getValue()), eq(executorId.getValue()));
    }
    
    @Test
    void lockTaskExecution_shouldReturnFalse_whenLockNotAcquired() {
        // Given
        TaskInstanceId taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
        when(distributedLock.tryLock(anyString(), anyInt())).thenReturn(false);
        
        // When
        boolean result = clusterCoordinator.lockTaskExecution(taskInstanceId, executorId);
        
        // Then
        assertFalse(result);
        verify(distributedLock).tryLock(eq("task_execution:" + taskInstanceId.getValue()), anyInt());
        verify(hashOps, never()).put(anyString(), anyString(), anyString());
    }
    
    @Test
    void releaseTaskExecution_shouldReturnTrue_whenLockReleased() {
        // Given
        TaskInstanceId taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
        when(distributedLock.releaseLock(anyString())).thenReturn(true);
        
        // When
        boolean result = clusterCoordinator.releaseTaskExecution(taskInstanceId);
        
        // Then
        assertTrue(result);
        verify(distributedLock).releaseLock(eq("task_execution:" + taskInstanceId.getValue()));
        verify(hashOps).delete(eq("task_locks"), eq(taskInstanceId.getValue()));
    }
    
    @Test
    void lockJobExecution_shouldReturnTrue_whenLockAcquired() {
        // Given
        JobId jobId = new JobId(UUID.randomUUID().toString());
        when(distributedLock.tryLock(anyString(), anyInt())).thenReturn(true);
        
        // When
        boolean result = clusterCoordinator.lockJobExecution(jobId);
        
        // Then
        assertTrue(result);
        verify(distributedLock).tryLock(eq("job_execution:" + jobId.getValue()), anyInt());
    }
    
    @Test
    void getTaskExecutor_shouldReturnExecutorId_whenTaskLocked() {
        // Given
        TaskInstanceId taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
        String executorIdValue = UUID.randomUUID().toString();
        when(hashOps.get(eq("task_locks"), eq(taskInstanceId.getValue()))).thenReturn(executorIdValue);
        
        // When
        Optional<ExecutorId> result = clusterCoordinator.getTaskExecutor(taskInstanceId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(executorIdValue, result.get().getValue());
    }
    
    @Test
    void getTaskExecutor_shouldReturnEmpty_whenTaskNotLocked() {
        // Given
        TaskInstanceId taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
        when(hashOps.get(eq("task_locks"), eq(taskInstanceId.getValue()))).thenReturn(null);
        
        // When
        Optional<ExecutorId> result = clusterCoordinator.getTaskExecutor(taskInstanceId);
        
        // Then
        assertFalse(result.isPresent());
    }
}