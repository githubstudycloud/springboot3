package com.example.demo.infrastructure.event;

import com.example.demo.domain.event.DomainEventPublisher;
import com.example.framework.domain.BaseDomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring实现的领域事件发布者
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    @Override
    public void publish(BaseDomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}