package com.example.platform.scheduler.application.service;

import com.example.platform.scheduler.application.dto.ExecutorDTO;
import com.example.platform.scheduler.application.dto.NodeInfoDTO;
import com.example.platform.scheduler.domain.common.DomainEventPublisher;
import com.example.platform.scheduler.domain.executor.*;
import com.example.platform.scheduler.domain.taskinstance.TaskInstanceId;
import com.example.platform.scheduler.domain.taskinstance.TaskInstanceRepository;
import com.example.platform.scheduler.infrastructure.cluster.ClusterCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterManagementServiceImplTest {

    @Mock
    private ExecutorRepository executorRepository;

    @Mock
    private TaskInstanceRepository taskInstanceRepository;

    @Mock
    private ClusterCoordinator clusterCoordinator;

    @Mock
    private DomainEventPublisher eventPublisher;

    private ClusterManagementServiceImpl clusterManagementService;

    @BeforeEach
    void setUp() {
        clusterManagementService = new ClusterManagementServiceImpl(
                executorRepository,
                taskInstanceRepository,
                clusterCoordinator,
                eventPublisher
        );
    }

    @Test
    void registerExecutor_shouldCreateAndSaveNewExecutor() {
        // Given
        String executorName = "test-executor-" + UUID.randomUUID().toString().substring(0, 8);
        NodeInfoDTO nodeInfo = new NodeInfoDTO("test-host", "127.0.0.1", "v1.0.0");
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("maxConcurrentTasks", 10);
        capabilities.put("supportsPriority", true);
        capabilities.put("supportsRetry", true);

        when(executorRepository.save(any(Executor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ExecutorDTO result = clusterManagementService.registerExecutor(executorName, nodeInfo, capabilities);

        // Then
        assertNotNull(result);
        assertEquals(executorName, result.getId());
        assertEquals(nodeInfo.getHost(), result.getNodeInfo().getHost());
        assertEquals(nodeInfo.getIpAddress(), result.getNodeInfo().getIpAddress());
        assertEquals(nodeInfo.getVersion(), result.getNodeInfo().getVersion());
        assertEquals(ExecutorStatus.REGISTERED, result.getStatus());

        ArgumentCaptor<Executor> executorCaptor = ArgumentCaptor.forClass(Executor.class);
        verify(executorRepository).save(executorCaptor.capture());

        Executor savedExecutor = executorCaptor.getValue();
        assertEquals(new ExecutorId(executorName), savedExecutor.getId());
        assertEquals(nodeInfo.getHost(), savedExecutor.getNodeInfo().getHost());
        assertEquals(nodeInfo.getIpAddress(), savedExecutor.getNodeInfo().getIpAddress());
        assertEquals(ExecutorStatus.REGISTERED, savedExecutor.getStatus());

        verify(clusterCoordinator).registerExecutor(any(ExecutorId.class), any(NodeInfo.class));
    }

    @Test
    void activateExecutor_shouldChangeExecutorStatusToOnline() {
        // Given
        ExecutorId executorId = new ExecutorId(UUID.randomUUID().toString());
        Executor executor = createRegisteredExecutor(executorId);

        when(executorRepository.findById(executorId)).thenReturn(Optional.of(executor));
        when(executorRepository.save(any(Executor.class))).thenReturn(executor);

        // When
        boolean result = clusterManagementService.activateExecutor(executorId.getValue());

        // Then
        assertTrue(result);
        assertEquals(ExecutorStatus.ONLINE, executor.getStatus());
        verify(executorRepository).save(executor);
    }

    @Test
    void deactivateExecutor_shouldChangeExecutorStatusToOffline() {
        // Given
        ExecutorId executorId = new ExecutorId(UUID.randomUUID().toString());
        Executor executor = createOnlineExecutor(executorId);

        when(executorRepository.findById(executorId)).thenReturn(Optional.of(executor));
        when(executorRepository.save(any(Executor.class))).thenReturn(executor);

        String reason = "Scheduled maintenance";

        // When
        boolean result = clusterManagementService.deactivateExecutor(executorId.getValue(), reason);

        // Then
        assertTrue(result);
        assertEquals(ExecutorStatus.OFFLINE, executor.getStatus());
        verify(executorRepository).save(executor);
    }

    @Test
    void updateExecutorHeartbeat_shouldUpdateHeartbeatTimeAndNotifyCluster() {
        // Given
        ExecutorId executorId = new ExecutorId(UUID.randomUUID().toString());
        Executor executor = createOnlineExecutor(executorId);

        when(executorRepository.findById(executorId)).thenReturn(Optional.of(executor));
        when(executorRepository.save(any(Executor.class))).thenReturn(executor);

        LocalDateTime beforeUpdate = executor.getLastHeartbeatTime();

        // Give some small delay to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        // When
        boolean result = clusterManagementService.updateExecutorHeartbeat(executorId.getValue());

        // Then
        assertTrue(result);
        assertTrue(executor.getLastHeartbeatTime().isAfter(beforeUpdate));
        verify(executorRepository).save(executor);
        verify(clusterCoordinator).updateExecutorHeartbeat(executorId);
    }

    @Test
    void getExecutorById_shouldReturnExecutorDTO_whenExecutorExists() {
        // Given
        ExecutorId executorId = new ExecutorId(UUID.randomUUID().toString());
        Executor executor = createOnlineExecutor(executorId);

        when(executorRepository.findById(executorId)).thenReturn(Optional.of(executor));

        // When
        Optional<ExecutorDTO> result = clusterManagementService.getExecutorById(executorId.getValue());

        // Then
        assertTrue(result.isPresent());
        ExecutorDTO executorDTO = result.get();
        assertEquals(executorId.getValue(), executorDTO.getId());
        assertEquals(executor.getNodeInfo().getHost(), executorDTO.getNodeInfo().getHost());
        assertEquals(executor.getNodeInfo().getIpAddress(), executorDTO.getNodeInfo().getIpAddress());
        assertEquals(executor.getStatus(), executorDTO.getStatus());
    }

    @Test
    void getExecutorById_shouldReturnEmpty_whenExecutorDoesNotExist() {
        // Given
        String executorId = UUID.randomUUID().toString();
        when(executorRepository.findById(any(ExecutorId.class))).thenReturn(Optional.empty());

        // When
        Optional<ExecutorDTO> result = clusterManagementService.getExecutorById(executorId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void getAllExecutors_shouldReturnAllExecutorsFromRepository() {
        // Given
        ExecutorId executorId1 = new ExecutorId(UUID.randomUUID().toString());
        ExecutorId executorId2 = new ExecutorId(UUID.randomUUID().toString());

        Executor executor1 = createOnlineExecutor(executorId1);
        Executor executor2 = createOfflineExecutor(executorId2);

        when(executorRepository.findAll()).thenReturn(Arrays.asList(executor1, executor2));

        // When
        List<ExecutorDTO> results = clusterManagementService.getAllExecutors();

        // Then
        assertEquals(2, results.size());

        // Verify the first executor
        ExecutorDTO executorDTO1 = results.get(0);
        assertEquals(executorId1.getValue(), executorDTO1.getId());
        assertEquals(ExecutorStatus.ONLINE, executorDTO1.getStatus());

        // Verify the second executor
        ExecutorDTO executorDTO2 = results.get(1);
        assertEquals(executorId2.getValue(), executorDTO2.getId());
        assertEquals(ExecutorStatus.OFFLINE, executorDTO2.getStatus());
    }

    @Test
    void getActiveExecutors_shouldReturnOnlyActiveExecutors() {
        // Given
        List<ExecutorId> activeExecutorIds = Arrays.asList(
                new ExecutorId(UUID.randomUUID().toString()),
                new ExecutorId(UUID.randomUUID().toString())
        );

        when(clusterCoordinator.getActiveExecutors()).thenReturn(activeExecutorIds);

        Executor executor1 = createOnlineExecutor(activeExecutorIds.get(0));
        Executor executor2 = createOnlineExecutor(activeExecutorIds.get(1));

        when(executorRepository.findById(activeExecutorIds.get(0))).thenReturn(Optional.of(executor1));
        when(executorRepository.findById(activeExecutorIds.get(1))).thenReturn(Optional.of(executor2));

        // When
        List<ExecutorDTO> results = clusterManagementService.getActiveExecutors();

        // Then
        assertEquals(2, results.size());
        for (ExecutorDTO executorDTO : results) {
            assertEquals(ExecutorStatus.ONLINE, executorDTO.getStatus());
        }
    }

    @Test
    void detectFailedExecutors_shouldMarkTimedOutExecutorsAsOffline() {
        // Given
        ExecutorId activeExecutorId = new ExecutorId(UUID.randomUUID().toString());
        ExecutorId timedOutExecutorId = new ExecutorId(UUID.randomUUID().toString());

        Executor activeExecutor = createOnlineExecutor(activeExecutorId);
        Executor timedOutExecutor = createOnlineExecutor(timedOutExecutorId);

        // Simulate last heartbeat was a long time ago for the timed out executor
        try {
            java.lang.reflect.Field lastHeartbeatField = Executor.class.getDeclaredField("lastHeartbeatTime");
            lastHeartbeatField.setAccessible(true);
            lastHeartbeatField.set(timedOutExecutor, LocalDateTime.now().minusMinutes(5));
        } catch (Exception e) {
            fail("Failed to set lastHeartbeatTime field: " + e.getMessage());
        }

        when(executorRepository.findByStatus(ExecutorStatus.ONLINE))
                .thenReturn(Arrays.asList(activeExecutor, timedOutExecutor));

        // When
        int failedCount = clusterManagementService.detectFailedExecutors(60); // 60 seconds timeout

        // Then
        assertEquals(1, failedCount);
        assertEquals(ExecutorStatus.OFFLINE, timedOutExecutor.getStatus());
        verify(executorRepository).save(timedOutExecutor);
    }

    @Test
    void getExecutorForTask_shouldReturnExecutorId_whenTaskExists() {
        // Given
        TaskInstanceId taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
        ExecutorId executorId = new ExecutorId(UUID.randomUUID().toString());

        when(clusterCoordinator.getTaskExecutor(taskInstanceId)).thenReturn(Optional.of(executorId));

        // When
        Optional<String> result = clusterManagementService.getExecutorForTask(taskInstanceId.getValue());

        // Then
        assertTrue(result.isPresent());
        assertEquals(executorId.getValue(), result.get());
    }

    // Helper methods to create samples for testing
    private Executor createRegisteredExecutor(ExecutorId executorId) {
        return Executor.register(
                executorId,
                new NodeInfo("test-host", "127.0.0.1", "v1.0.0"),
                new ExecutorCapability(10, true, true),
                eventPublisher
        );
    }

    private Executor createOnlineExecutor(ExecutorId executorId) {
        Executor executor = createRegisteredExecutor(executorId);
        executor.goOnline();
        return executor;
    }

    private Executor createOfflineExecutor(ExecutorId executorId) {
        Executor executor = createOnlineExecutor(executorId);
        executor.goOffline("Test offline reason");
        return executor;
    }
}