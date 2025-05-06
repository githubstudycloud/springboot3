package com.example.platform.scheduler.domain.job;

import com.example.platform.scheduler.domain.common.DomainEventPublisher;
import com.example.platform.scheduler.domain.job.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobTest {

    @Mock
    private DomainEventPublisher eventPublisher;

    private JobId jobId;
    private String jobName;
    private String description;
    private JobType jobType;
    private JobTrigger trigger;
    private JobPriority priority;
    private JobParameters parameters;
    private JobRetryPolicy retryPolicy;
    private List<JobDependency> dependencies;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobId = new JobId(UUID.randomUUID().toString());
        jobName = "TestJob";
        description = "Test job description";
        jobType = JobType.DATA_PROCESSING;
        
        // Create a cron trigger for every hour
        trigger = JobTrigger.cron("0 0 * * * ?", "UTC");
        
        priority = JobPriority.MEDIUM;
        parameters = new JobParameters("{\"param1\": \"value1\", \"param2\": 123}");
        retryPolicy = new JobRetryPolicy(3, 60, JobRetryStrategy.EXPONENTIAL_BACKOFF);
        
        // Create a simple dependency
        JobId dependencyJobId = new JobId(UUID.randomUUID().toString());
        dependencies = Arrays.asList(
            new JobDependency(
                dependencyJobId, 
                JobDependencyType.COMPLETION, 
                "{}"
            )
        );
    }

    @Test
    void createJob_shouldInitializeCorrectly() {
        // When
        Job job = Job.create(
            jobId,
            jobName,
            description,
            jobType,
            trigger,
            priority,
            parameters,
            retryPolicy,
            dependencies,
            eventPublisher
        );

        // Then
        assertEquals(jobId, job.getId());
        assertEquals(jobName, job.getName());
        assertEquals(description, job.getDescription());
        assertEquals(jobType, job.getJobType());
        assertEquals(trigger, job.getTrigger());
        assertEquals(priority, job.getPriority());
        assertEquals(parameters, job.getParameters());
        assertEquals(retryPolicy, job.getRetryPolicy());
        assertEquals(dependencies, job.getDependencies());
        assertEquals(JobStatus.CREATED, job.getStatus());
        verify(eventPublisher, times(1)).publish(any(JobCreatedEvent.class));
    }

    @Test
    void activateJob_shouldChangeStatusToActive() {
        // Given
        Job job = createJob();

        // When
        job.activate();

        // Then
        assertEquals(JobStatus.ACTIVE, job.getStatus());
        verify(eventPublisher, times(1)).publish(any(JobActivatedEvent.class));
    }

    @Test
    void pauseJob_shouldChangeStatusToPaused() {
        // Given
        Job job = createActiveJob();
        String reason = "Maintenance pause";

        // When
        job.pause(reason);

        // Then
        assertEquals(JobStatus.PAUSED, job.getStatus());
        verify(eventPublisher, times(1)).publish(any(JobPausedEvent.class));
    }

    @Test
    void resumeJob_shouldChangeStatusToActive() {
        // Given
        Job job = createPausedJob();

        // When
        job.resume();

        // Then
        assertEquals(JobStatus.ACTIVE, job.getStatus());
        verify(eventPublisher, times(1)).publish(any(JobResumedEvent.class));
    }

    @Test
    void disableJob_shouldChangeStatusToDisabled() {
        // Given
        Job job = createActiveJob();
        String reason = "Job no longer needed";

        // When
        job.disable(reason);

        // Then
        assertEquals(JobStatus.DISABLED, job.getStatus());
        verify(eventPublisher, times(1)).publish(any(JobDisabledEvent.class));
    }

    @Test
    void updateTrigger_shouldUpdateJobTrigger() {
        // Given
        Job job = createActiveJob();
        JobTrigger newTrigger = JobTrigger.cron("0 0 0 * * ?", "UTC");

        // When
        job.updateTrigger(newTrigger);

        // Then
        assertEquals(newTrigger, job.getTrigger());
        verify(eventPublisher, times(1)).publish(any(JobTriggerUpdatedEvent.class));
    }

    @Test
    void updateParameters_shouldUpdateJobParameters() {
        // Given
        Job job = createActiveJob();
        JobParameters newParameters = new JobParameters("{\"newParam\": \"newValue\"}");

        // When
        job.updateParameters(newParameters);

        // Then
        assertEquals(newParameters, job.getParameters());
        verify(eventPublisher, times(1)).publish(any(JobParametersUpdatedEvent.class));
    }

    @Test
    void updateRetryPolicy_shouldUpdateRetryPolicy() {
        // Given
        Job job = createActiveJob();
        JobRetryPolicy newRetryPolicy = new JobRetryPolicy(5, 30, JobRetryStrategy.FIXED_DELAY);

        // When
        job.updateRetryPolicy(newRetryPolicy);

        // Then
        assertEquals(newRetryPolicy, job.getRetryPolicy());
        verify(eventPublisher, times(1)).publish(any(JobRetryPolicyUpdatedEvent.class));
    }

    @Test
    void updateDependencies_shouldUpdateDependencies() {
        // Given
        Job job = createActiveJob();
        JobId newDependencyJobId = new JobId(UUID.randomUUID().toString());
        List<JobDependency> newDependencies = Arrays.asList(
            new JobDependency(
                newDependencyJobId, 
                JobDependencyType.COMPLETION_WITH_SUCCESS, 
                "{}"
            )
        );

        // When
        job.updateDependencies(newDependencies);

        // Then
        assertEquals(newDependencies, job.getDependencies());
        verify(eventPublisher, times(1)).publish(any(JobDependenciesUpdatedEvent.class));
    }

    @Test
    void updatePriority_shouldUpdateJobPriority() {
        // Given
        Job job = createActiveJob();
        JobPriority newPriority = JobPriority.HIGH;

        // When
        job.updatePriority(newPriority);

        // Then
        assertEquals(newPriority, job.getPriority());
        verify(eventPublisher, times(1)).publish(any(JobPriorityUpdatedEvent.class));
    }

    @Test
    void recordExecution_shouldUpdateLastExecutionInfo() {
        // Given
        Job job = createActiveJob();
        LocalDateTime executionTime = LocalDateTime.now();
        JobExecutionStatus executionStatus = JobExecutionStatus.SUCCESS;

        // When
        job.recordExecution(executionTime, executionStatus);

        // Then
        assertEquals(executionTime, job.getLastExecutionTime());
        assertEquals(executionStatus, job.getLastExecutionStatus());
        verify(eventPublisher, times(1)).publish(any(JobExecutionRecordedEvent.class));
    }

    @Test
    void scheduleNextExecution_shouldUpdateNextExecutionTime() {
        // Given
        Job job = createActiveJob();
        LocalDateTime nextExecutionTime = LocalDateTime.now().plusHours(1);

        // When
        job.scheduleNextExecution(nextExecutionTime);

        // Then
        assertEquals(nextExecutionTime, job.getNextScheduledExecutionTime());
        verify(eventPublisher, times(1)).publish(any(JobScheduledEvent.class));
    }

    @Test
    void shouldNotActivateDisabledJob() {
        // Given
        Job job = createJob();
        job.disable("Testing disabled status");
        reset(eventPublisher); // Clear previous events

        // When/Then
        assertThrows(IllegalStateException.class, () -> job.activate());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void isActive_shouldReturnTrueForActiveJob() {
        // Given
        Job job = createActiveJob();

        // When
        boolean result = job.isActive();

        // Then
        assertTrue(result);
    }

    @Test
    void isActive_shouldReturnFalseForNonActiveJob() {
        // Given
        Job job = createPausedJob();

        // When
        boolean result = job.isActive();

        // Then
        assertFalse(result);
    }

    @Test
    void canExecute_shouldReturnTrueForActiveJobWithNoMissingDependencies() {
        // Given
        Job job = createActiveJob();
        // Assuming no missing dependencies for simplicity
        
        // When
        boolean result = job.canExecute();
        
        // Then
        assertTrue(result);
    }

    // Helper methods to create jobs in different states
    private Job createJob() {
        return Job.create(
            jobId,
            jobName,
            description,
            jobType,
            trigger,
            priority,
            parameters,
            retryPolicy,
            dependencies,
            eventPublisher
        );
    }

    private Job createActiveJob() {
        Job job = createJob();
        reset(eventPublisher); // Reset to clear the creation event
        job.activate();
        reset(eventPublisher); // Reset again for the next operation
        return job;
    }

    private Job createPausedJob() {
        Job job = createActiveJob();
        job.pause("Test pause reason");
        reset(eventPublisher); // Reset to clear the pause event
        return job;
    }

    private Job createDisabledJob() {
        Job job = createActiveJob();
        job.disable("Test disable reason");
        reset(eventPublisher); // Reset to clear the disable event
        return job;
    }
}
