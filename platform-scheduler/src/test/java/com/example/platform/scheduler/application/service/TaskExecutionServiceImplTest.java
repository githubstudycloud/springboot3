package com.example.platform.scheduler.application.service;

import com.example.platform.scheduler.application.dto.TaskInstanceDTO;
import com.example.platform.scheduler.domain.common.DomainEventPublisher;
import com.example.platform.scheduler.domain.executor.Executor;
import com.example.platform.scheduler.domain.executor.ExecutorId;
import com.example.platform.scheduler.domain.executor.ExecutorRepository;
import com.example.platform.scheduler.domain.job.*;
import com.example.platform.scheduler.domain.taskinstance.*;
import com.example.platform.scheduler.infrastructure.cluster.ClusterCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskExecutionServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private TaskInstanceRepository taskInstanceRepository;

    @Mock
    private ExecutorRepository executorRepository;

    @Mock
    private ClusterCoordinator clusterCoordinator;

    @Mock
    private DomainEventPublisher eventPublisher;

    private TaskExecutionServiceImpl taskExecutionService;

    private JobId jobId;
    private ExecutorId executorId;
    private TaskInstanceId taskInstanceId;

    @BeforeEach
    void setUp() {
        taskExecutionService = new TaskExecutionServiceImpl(
                jobRepository,
                taskInstanceRepository,
                executorRepository,
                clusterCoordinator,
                eventPublisher
        );

        jobId = new JobId(UUID.randomUUID().toString());
        executorId = new ExecutorId(UUID.randomUUID().toString());
        taskInstanceId = new TaskInstanceId(UUID.randomUUID().toString());
    }

    @Test
    void scheduleTask_shouldCreateAndSaveNewTaskInstance() {
        // Given
        Job job = createSampleJob(jobId);
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(5);
        TaskParameters parameters = new TaskParameters("{\"param1\": \"value1\", \"param2\": 123}");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TaskInstanceDTO result = taskExecutionService.scheduleTask(
                jobId.getValue(),
                scheduledTime,
                parameters.getValue()
        );

        // Then
        assertNotNull(result);
        assertEquals(jobId.getValue(), result.getJobId());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        assertEquals(parameters.getValue(), result.getParameters());

        ArgumentCaptor<TaskInstance> taskCaptor = ArgumentCaptor.forClass(TaskInstance.class);
        verify(taskInstanceRepository).save(taskCaptor.capture());

        TaskInstance savedTask = taskCaptor.getValue();
        assertEquals(jobId, savedTask.getJobId());
        assertEquals(job.getName(), savedTask.getTaskName());
        assertEquals(scheduledTime, savedTask.getScheduledTime());
        assertEquals(parameters, savedTask.getParameters());
    }

    @Test
    void assignTask_shouldAssignExecutorToTask() {
        // Given
        TaskInstance taskInstance = createPendingTaskInstance(taskInstanceId, jobId);
        Executor executor = createOnlineExecutor(executorId);

        when(taskInstanceRepository.findById(taskInstanceId)).thenReturn(Optional.of(taskInstance));
        when(executorRepository.findById(executorId)).thenReturn(Optional.of(executor));
        when(clusterCoordinator.lockTaskExecution(taskInstanceId, executorId)).thenReturn(true);
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = taskExecutionService.assignTask(taskInstanceId.getValue(), executorId.getValue());

        // Then
        assertTrue(result);
        assertEquals(executorId, taskInstance.getExecutorId());
        assertEquals(TaskStatus.ASSIGNED, taskInstance.getStatus());
        verify(taskInstanceRepository).save(taskInstance);
    }

    @Test
    void startTask_shouldUpdateTaskStatusToRunning() {
        // Given
        TaskInstance taskInstance = createAssignedTaskInstance(taskInstanceId, jobId, executorId);

        when(taskInstanceRepository.findById(taskInstanceId)).thenReturn(Optional.of(taskInstance));
        when(clusterCoordinator.getTaskExecutor(taskInstanceId)).thenReturn(Optional.of(executorId));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = taskExecutionService.startTask(taskInstanceId.getValue(), executorId.getValue());

        // Then
        assertTrue(result);
        assertEquals(TaskStatus.RUNNING, taskInstance.getStatus());
        assertNotNull(taskInstance.getStartTime());
        verify(taskInstanceRepository).save(taskInstance);
    }

    @Test
    void completeTask_shouldUpdateTaskStatusToCompleted() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance(taskInstanceId, jobId, executorId);
        TaskResult result = new TaskResult("{\"result\": \"success\"}");

        when(taskInstanceRepository.findById(taskInstanceId)).thenReturn(Optional.of(taskInstance));
        when(clusterCoordinator.getTaskExecutor(taskInstanceId)).thenReturn(Optional.of(executorId));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(createSampleJob(jobId)));

        // When
        boolean completed = taskExecutionService.completeTask(taskInstanceId.getValue(), executorId.getValue(), result.getValue());

        // Then
        assertTrue(completed);
        assertEquals(TaskStatus.COMPLETED, taskInstance.getStatus());
        assertEquals(result, taskInstance.getResult());
        assertNotNull(taskInstance.getEndTime());
        verify(taskInstanceRepository).save(taskInstance);
        verify(clusterCoordinator).releaseTaskExecution(taskInstanceId);
        
        // Verify job execution was recorded
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void failTask_shouldUpdateTaskStatusToFailed() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance(taskInstanceId, jobId, executorId);
        String errorMessage = "Test error message";

        when(taskInstanceRepository.findById(taskInstanceId)).thenReturn(Optional.of(taskInstance));
        when(clusterCoordinator.getTaskExecutor(taskInstanceId)).thenReturn(Optional.of(executorId));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(createSampleJob(jobId)));

        // When
        boolean result = taskExecutionService.failTask(taskInstanceId.getValue(), executorId.getValue(), errorMessage);

        // Then
        assertTrue(result);
        assertEquals(TaskStatus.FAILED, taskInstance.getStatus());
        assertNotNull(taskInstance.getEndTime());
        verify(taskInstanceRepository).save(taskInstance);
        verify(clusterCoordinator).releaseTaskExecution(taskInstanceId);
        
        // Verify job execution was recorded
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void cancelTask_shouldUpdateTaskStatusToCancelled() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance(taskInstanceId, jobId, executorId);
        String reason = "Test cancellation reason";

        when(taskInstanceRepository.findById(taskInstanceId)).thenReturn(Optional.of(taskInstance));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = taskExecutionService.cancelTask(taskInstanceId.getValue(), reason);

        // Then
        assertTrue(result);
        assertEquals(TaskStatus.CANCELLED, taskInstance.getStatus());
        assertNotNull(taskInstance.getEndTime());
        verify(taskInstanceRepository).save(taskInstance);
        verify(clusterCoordinator).releaseTaskExecution(taskInstanceId);
    }

    @Test
    void getTaskById_shouldReturnTaskDTO_whenTaskExists() {
        // Given
        TaskInstance taskInstance = createCompletedTaskInstance(taskInstanceId, jobId, executorId);
        
        when(taskInstanceRepository.findById(taskInstanceId)).thenReturn(Optional.of(taskInstance));
        
        // When
        Optional<TaskInstanceDTO> result = taskExecutionService.getTaskById(taskInstanceId.getValue());
        
        // Then
        assertTrue(result.isPresent());
        TaskInstanceDTO taskDTO = result.get();
        assertEquals(taskInstanceId.getValue(), taskDTO.getId());
        assertEquals(jobId.getValue(), taskDTO.getJobId());
        assertEquals(executorId.getValue(), taskDTO.getExecutorId());
        assertEquals(TaskStatus.COMPLETED, taskDTO.getStatus());
    }

    @Test
    void addTaskLog_shouldAddLogEntryToTask() {
        // Given
        TaskInstance taskInstance = createRunningTaskInstance(taskInstanceId, jobId, executorId);
        String logMessage = "Test log message";
        TaskLogLevel logLevel = TaskLogLevel.INFO;

        when(taskInstanceRepository.findById(taskInstanceId)).thenReturn(Optional.of(taskInstance));
        when(clusterCoordinator.getTaskExecutor(taskInstanceId)).thenReturn(Optional.of(executorId));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        boolean result = taskExecutionService.addTaskLog(
                taskInstanceId.getValue(),
                executorId.getValue(),
                logLevel.name(),
                logMessage
        );

        // Then
        assertTrue(result);
        assertFalse(taskInstance.getLogs().isEmpty());
        TaskLogEntry lastEntry = taskInstance.getLogs().get(taskInstance.getLogs().size() - 1);
        assertEquals(logLevel, lastEntry.getLevel());
        assertEquals(logMessage, lastEntry.getMessage());
        verify(taskInstanceRepository).save(taskInstance);
    }

    // Helper methods to create samples for testing
    private Job createSampleJob(JobId jobId) {
        String jobName = "Test Job";
        String description = "Test job description";
        JobType jobType = JobType.DATA_PROCESSING;
        JobTrigger trigger = JobTrigger.cron("0 0 * * * ?", "UTC");
        JobPriority priority = JobPriority.MEDIUM;
        JobParameters parameters = new JobParameters("{\"param1\": \"value1\", \"param2\": 123}");
        JobRetryPolicy retryPolicy = new JobRetryPolicy(3, 60, JobRetryStrategy.EXPONENTIAL_BACKOFF);
        
        return Job.create(
                jobId,
                jobName,
                description,
                jobType,
                trigger,
                priority,
                parameters,
                retryPolicy,
                Collections.emptyList(),
                eventPublisher
        );
    }

    private TaskInstance createPendingTaskInstance(TaskInstanceId taskInstanceId, JobId jobId) {
        return TaskInstance.create(
                taskInstanceId,
                jobId,
                "Test Task",
                LocalDateTime.now(),
                new TaskParameters("{\"param1\": \"value1\", \"param2\": 123}"),
                300L,
                eventPublisher
        );
    }

    private TaskInstance createAssignedTaskInstance(TaskInstanceId taskInstanceId, JobId jobId, ExecutorId executorId) {
        TaskInstance taskInstance = createPendingTaskInstance(taskInstanceId, jobId);
        taskInstance.assignExecutor(executorId);
        return taskInstance;
    }

    private TaskInstance createRunningTaskInstance(TaskInstanceId taskInstanceId, JobId jobId, ExecutorId executorId) {
        TaskInstance taskInstance = createAssignedTaskInstance(taskInstanceId, jobId, executorId);
        taskInstance.startExecution();
        return taskInstance;
    }

    private TaskInstance createCompletedTaskInstance(TaskInstanceId taskInstanceId, JobId jobId, ExecutorId executorId) {
        TaskInstance taskInstance = createRunningTaskInstance(taskInstanceId, jobId, executorId);
        taskInstance.completeExecution(new TaskResult("{\"result\": \"success\"}"));
        return taskInstance;
    }

    private Executor createOnlineExecutor(ExecutorId executorId) {
        Executor executor = Executor.register(
                executorId,
                new NodeInfo("test-host", "127.0.0.1", "v1.0.0"),
                new ExecutorCapability(10, true, true),
                eventPublisher
        );
        executor.goOnline();
        return executor;
    }
}