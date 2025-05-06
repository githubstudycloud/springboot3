package com.example.platform.scheduler.domain.taskinstance;

import com.example.platform.scheduler.domain.common.DomainEventPublisher;
import com.example.platform.scheduler.domain.taskinstance.event.*;
import com.example.platform.scheduler.domain.executor.ExecutorId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskInstanceTest {

    @Mock
    private DomainEventPublisher eventPublisher;

    private TaskInstanceId taskInstanceId;
    private JobId jobId;
    private LocalDateTime scheduledTime;
    private TaskParameters parameters;
    private ExecutorId executorId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
        jobId = new JobId(UUID.randomUUID().toString());
        scheduledTime = LocalDateTime.now();
        parameters = new TaskParameters("{\"param1\": \"value1\", \"param2\": 123}");
        executorId = new ExecutorId(UUID.randomUUID().toString());
    }

    @Test
    void createNewInstance_shouldSetCorrectState() {
        // Given
        String taskName = "TestTask";
        long timeoutSeconds = 300L;

        // When
        TaskInstance taskInstance = TaskInstance.create(
            taskInstanceId,
            jobId,
            taskName,
            scheduledTime,
            parameters,
            timeoutSeconds,
            eventPublisher
        );

        // Then
        assertEquals(taskInstanceId, taskInstance.getId());
        assertEquals(jobId, taskInstance.getJobId());
        assertEquals(taskName, taskInstance.getTaskName());
        assertEquals(parameters, taskInstance.getParameters());
        assertEquals(TaskStatus.PENDING, taskInstance.getStatus());
        assertEquals(timeoutSeconds, taskInstance.getTimeoutSeconds());
        verify(eventPublisher, times(1)).publish(any(TaskInstanceCreatedEvent.class));
    }

    @Test
    void assignExecutor_shouldUpdateExecutorAndChangeStatus() {
        // Given
        TaskInstance taskInstance = createPendingTaskInstance();

        // When
        taskInstance.assignExecutor(executorId);

        // Then
        assertEquals(executorId, taskInstance.getExecutorId());
        assertEquals(TaskStatus.ASSIGNED, taskInstance.getStatus());
        verify(eventPublisher, times(1)).publish(any(TaskInstanceAssignedEvent.class));
    }

    @Test
    void startExecution_shouldChangeStatusToRunning() {
        // Given
        TaskInstance taskInstance = createAssignedTaskInstance();

        // When
        taskInstance.startExecution();

        // Then
        assertEquals(TaskStatus.RUNNING, taskInstance.getStatus());
        assertNotNull(taskInstance.getStartTime());
        verify(eventPublisher, times(1)).publish(any(TaskInstanceStartedEvent.class));
    }

    @Test
    void completeExecution_shouldChangeStatusToCompleted() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance();
        TaskResult result = new TaskResult("{\"result\": \"success\"}");

        // When
        taskInstance.completeExecution(result);

        // Then
        assertEquals(TaskStatus.COMPLETED, taskInstance.getStatus());
        assertEquals(result, taskInstance.getResult());
        assertNotNull(taskInstance.getEndTime());
        verify(eventPublisher, times(1)).publish(any(TaskInstanceCompletedEvent.class));
    }

    @Test
    void failExecution_shouldChangeStatusToFailed() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance();
        String errorMessage = "Test error message";

        // When
        taskInstance.failExecution(errorMessage);

        // Then
        assertEquals(TaskStatus.FAILED, taskInstance.getStatus());
        assertNotNull(taskInstance.getEndTime());
        verify(eventPublisher, times(1)).publish(any(TaskInstanceFailedEvent.class));
    }

    @Test
    void cancelExecution_shouldChangeStatusToCancelled() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance();
        String reason = "Test cancellation reason";

        // When
        taskInstance.cancelExecution(reason);

        // Then
        assertEquals(TaskStatus.CANCELLED, taskInstance.getStatus());
        assertNotNull(taskInstance.getEndTime());
        verify(eventPublisher, times(1)).publish(any(TaskInstanceCancelledEvent.class));
    }

    @Test
    void addLogEntry_shouldAddEntryToLogs() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance();
        String logMessage = "Test log message";
        TaskLogLevel logLevel = TaskLogLevel.INFO;

        // When
        taskInstance.addLogEntry(logLevel, logMessage);

        // Then
        assertFalse(taskInstance.getLogs().isEmpty());
        TaskLogEntry lastEntry = taskInstance.getLogs().get(taskInstance.getLogs().size() - 1);
        assertEquals(logLevel, lastEntry.getLevel());
        assertEquals(logMessage, lastEntry.getMessage());
    }

    @Test
    void isTimedOut_shouldReturnTrueWhenExecutionExceedsTimeout() {
        // Given
        long timeoutSeconds = 5L;
        TaskInstance taskInstance = TaskInstance.create(
            taskInstanceId,
            jobId,
            "TimeoutTask",
            scheduledTime,
            parameters,
            timeoutSeconds,
            eventPublisher
        );
        
        taskInstance.assignExecutor(executorId);
        taskInstance.startExecution();
        
        // Simulate task started 10 seconds ago
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenSecondsAgo = now.minusSeconds(10);
        // Use reflection to set the startTime to 10 seconds ago
        // This is a test-only approach, not recommended for production code
        try {
            java.lang.reflect.Field startTimeField = TaskInstance.class.getDeclaredField("startTime");
            startTimeField.setAccessible(true);
            startTimeField.set(taskInstance, tenSecondsAgo);
        } catch (Exception e) {
            fail("Failed to set startTime field: " + e.getMessage());
        }

        // When
        boolean result = taskInstance.isTimedOut();

        // Then
        assertTrue(result);
    }

    @Test
    void isTimedOut_shouldReturnFalseWhenExecutionWithinTimeout() {
        // Given
        long timeoutSeconds = 30L;
        TaskInstance taskInstance = createRunningTaskInstance(timeoutSeconds);

        // When
        boolean result = taskInstance.isTimedOut();

        // Then
        assertFalse(result);
    }

    // Helper methods to create task instances in different states
    private TaskInstance createPendingTaskInstance() {
        return TaskInstance.create(
            taskInstanceId,
            jobId,
            "TestTask",
            scheduledTime,
            parameters,
            300L,
            eventPublisher
        );
    }

    private TaskInstance createAssignedTaskInstance() {
        TaskInstance taskInstance = createPendingTaskInstance();
        reset(eventPublisher); // Reset to clear the creation event
        taskInstance.assignExecutor(executorId);
        reset(eventPublisher); // Reset again to start fresh for the next operation
        return taskInstance;
    }

    private TaskInstance createRunningTaskInstance() {
        return createRunningTaskInstance(300L);
    }

    private TaskInstance createRunningTaskInstance(long timeoutSeconds) {
        TaskInstance taskInstance = TaskInstance.create(
            taskInstanceId,
            jobId,
            "TestTask",
            scheduledTime,
            parameters,
            timeoutSeconds,
            eventPublisher
        );
        taskInstance.assignExecutor(executorId);
        reset(eventPublisher); // Clear previous events
        taskInstance.startExecution();
        reset(eventPublisher); // Reset again for the next operation
        return taskInstance;
    }
}
