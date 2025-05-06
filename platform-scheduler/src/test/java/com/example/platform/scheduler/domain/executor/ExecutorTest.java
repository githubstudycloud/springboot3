package com.example.platform.scheduler.domain.executor;

import com.example.platform.scheduler.domain.common.DomainEventPublisher;
import com.example.platform.scheduler.domain.executor.event.ExecutorHeartbeatEvent;
import com.example.platform.scheduler.domain.executor.event.ExecutorOfflineEvent;
import com.example.platform.scheduler.domain.executor.event.ExecutorOnlineEvent;
import com.example.platform.scheduler.domain.executor.event.ExecutorRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExecutorTest {

    @Mock
    private DomainEventPublisher eventPublisher;

    private ExecutorId executorId;
    private NodeInfo nodeInfo;
    private ExecutorCapability capability;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        executorId = new ExecutorId(UUID.randomUUID().toString());
        nodeInfo = new NodeInfo("test-host", "127.0.0.1", "v1.0.0");
        capability = new ExecutorCapability(10, true, true);
    }

    @Test
    void register_shouldCreateNewExecutor() {
        // When
        Executor executor = Executor.register(executorId, nodeInfo, capability, eventPublisher);

        // Then
        assertEquals(executorId, executor.getId());
        assertEquals(nodeInfo, executor.getNodeInfo());
        assertEquals(capability, executor.getCapability());
        assertEquals(ExecutorStatus.REGISTERED, executor.getStatus());
        verify(eventPublisher, times(1)).publish(any(ExecutorRegisteredEvent.class));
    }

    @Test
    void goOnline_shouldChangeStatusToOnline() {
        // Given
        Executor executor = createRegisteredExecutor();

        // When
        executor.goOnline();

        // Then
        assertEquals(ExecutorStatus.ONLINE, executor.getStatus());
        verify(eventPublisher, times(1)).publish(any(ExecutorOnlineEvent.class));
    }

    @Test
    void goOffline_shouldChangeStatusToOffline() {
        // Given
        Executor executor = createOnlineExecutor();
        String reason = "Scheduled maintenance";

        // When
        executor.goOffline(reason);

        // Then
        assertEquals(ExecutorStatus.OFFLINE, executor.getStatus());
        verify(eventPublisher, times(1)).publish(any(ExecutorOfflineEvent.class));
    }

    @Test
    void updateHeartbeat_shouldUpdateLastHeartbeatTime() {
        // Given
        Executor executor = createOnlineExecutor();
        LocalDateTime beforeUpdate = executor.getLastHeartbeatTime();
        
        // Give some small delay to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        // When
        executor.updateHeartbeat();

        // Then
        assertTrue(executor.getLastHeartbeatTime().isAfter(beforeUpdate));
        verify(eventPublisher, times(1)).publish(any(ExecutorHeartbeatEvent.class));
    }

    @Test
    void updateCapability_shouldUpdateCapabilityInformation() {
        // Given
        Executor executor = createOnlineExecutor();
        ExecutorCapability newCapability = new ExecutorCapability(20, false, true);

        // When
        executor.updateCapability(newCapability);

        // Then
        assertEquals(newCapability, executor.getCapability());
    }

    @Test
    void isActive_shouldReturnTrueForOnlineExecutor() {
        // Given
        Executor executor = createOnlineExecutor();

        // When
        boolean result = executor.isActive();

        // Then
        assertTrue(result);
    }

    @Test
    void isActive_shouldReturnFalseForOfflineExecutor() {
        // Given
        Executor executor = createOfflineExecutor();

        // When
        boolean result = executor.isActive();

        // Then
        assertFalse(result);
    }

    @Test
    void hasTimedOut_shouldReturnTrueWhenHeartbeatExceedsTimeout() {
        // Given
        Executor executor = createOnlineExecutor();
        
        // Simulate last heartbeat was a long time ago
        try {
            java.lang.reflect.Field lastHeartbeatField = Executor.class.getDeclaredField("lastHeartbeatTime");
            lastHeartbeatField.setAccessible(true);
            lastHeartbeatField.set(executor, LocalDateTime.now().minusMinutes(5));
        } catch (Exception e) {
            fail("Failed to set lastHeartbeatTime field: " + e.getMessage());
        }

        // When
        boolean result = executor.hasTimedOut(60); // 60 seconds timeout

        // Then
        assertTrue(result);
    }

    @Test
    void hasTimedOut_shouldReturnFalseWhenHeartbeatWithinTimeout() {
        // Given
        Executor executor = createOnlineExecutor();
        
        // Recent heartbeat
        executor.updateHeartbeat();

        // When
        boolean result = executor.hasTimedOut(60); // 60 seconds timeout

        // Then
        assertFalse(result);
    }

    @Test
    void canHandle_shouldReturnTrueWhenHasAvailableSlots() {
        // Given
        Executor executor = createOnlineExecutor();

        // When
        boolean result = executor.canHandleAdditionalTask();

        // Then
        assertTrue(result);
    }

    @Test
    void canHandle_shouldReturnFalseWhenNoAvailableSlots() {
        // Given
        Executor executor = createOnlineExecutor();
        
        // Set current tasks to max capacity
        try {
            java.lang.reflect.Field currentTasksField = Executor.class.getDeclaredField("currentTasks");
            currentTasksField.setAccessible(true);
            currentTasksField.set(executor, executor.getCapability().getMaxConcurrentTasks());
        } catch (Exception e) {
            fail("Failed to set currentTasks field: " + e.getMessage());
        }

        // When
        boolean result = executor.canHandleAdditionalTask();

        // Then
        assertFalse(result);
    }

    // Helper methods to create executors in different states
    private Executor createRegisteredExecutor() {
        Executor executor = Executor.register(executorId, nodeInfo, capability, eventPublisher);
        reset(eventPublisher); // Reset to clear the registration event
        return executor;
    }

    private Executor createOnlineExecutor() {
        Executor executor = createRegisteredExecutor();
        executor.goOnline();
        reset(eventPublisher); // Reset to clear the online event
        return executor;
    }

    private Executor createOfflineExecutor() {
        Executor executor = createOnlineExecutor();
        executor.goOffline("Test offline reason");
        reset(eventPublisher); // Reset to clear the offline event
        return executor;
    }
}
