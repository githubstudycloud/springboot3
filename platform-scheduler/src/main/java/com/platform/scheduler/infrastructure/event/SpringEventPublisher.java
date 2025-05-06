package com.platform.scheduler.infrastructure.event;

import com.platform.scheduler.domain.event.common.DomainEvent;
import com.platform.scheduler.domain.event.common.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Spring事件发布器实现
 * 使用Spring的事件机制实现领域事件发布
 *
 * @author platform
 */
@Component
public class SpringEventPublisher implements DomainEventPublisher, ApplicationEventPublisherAware {
    
    private static final Logger logger = LoggerFactory.getLogger(SpringEventPublisher.class);
    
    private ApplicationEventPublisher applicationEventPublisher;
    
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    @Override
    public void publish(DomainEvent event) {
        if (event == null) {
            logger.warn("Attempted to publish null domain event");
            return;
        }
        
        logger.debug("Publishing domain event: {}", event.getEventType());
        this.applicationEventPublisher.publishEvent(new DomainEventWrapper(event));
    }
    
    /**
     * 领域事件包装器
     * 将领域事件包装为Spring事件
     */
    public static class DomainEventWrapper {
        
        private final DomainEvent domainEvent;
        
        public DomainEventWrapper(DomainEvent domainEvent) {
            this.domainEvent = domainEvent;
        }
        
        public DomainEvent getDomainEvent() {
            return domainEvent;
        }
    }
}
