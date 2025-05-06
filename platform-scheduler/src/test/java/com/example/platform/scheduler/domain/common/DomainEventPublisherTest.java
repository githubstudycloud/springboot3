package com.example.platform.scheduler.domain.common;

import com.example.platform.scheduler.domain.job.JobId;
import com.example.platform.scheduler.domain.job.event.JobCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class DomainEventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private DomainEventPublisher domainEventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        domainEventPublisher = new DomainEventPublisher(applicationEventPublisher);
    }

    @Test
    void publish_shouldForwardEventToApplicationEventPublisher() {
        // Given
        JobId jobId = new JobId(UUID.randomUUID().toString());
        String jobName = "TestJob";
        JobCreatedEvent event = new JobCreatedEvent(
            jobId,
            jobName,
            LocalDateTime.now()
        );

        // When
        domainEventPublisher.publish(event);

        // Then
        ArgumentCaptor<JobCreatedEvent> eventCaptor = ArgumentCaptor.forClass(JobCreatedEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        
        JobCreatedEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(jobId, capturedEvent.getJobId());
        assertEquals(jobName, capturedEvent.getJobName());
    }
}
