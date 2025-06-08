package com.platform.domain.config.shared;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 领域事件发布器
 * DDD基础设施 - 负责发布领域事件到Spring事件总线
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
public class DomainEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    /**
     * 发布单个领域事件
     */
    public void publish(DomainEvent event) {
        if (event != null) {
            applicationEventPublisher.publishEvent(event);
        }
    }
    
    /**
     * 批量发布领域事件
     */
    public void publishAll(Collection<DomainEvent> events) {
        if (events != null && !events.isEmpty()) {
            events.forEach(this::publish);
        }
    }
    
    /**
     * 发布带延迟的领域事件
     */
    public void publishAsync(DomainEvent event) {
        if (event != null && event.isAsyncProcessing()) {
            // 包装为异步事件
            AsyncDomainEventWrapper wrapper = new AsyncDomainEventWrapper(event);
            applicationEventPublisher.publishEvent(wrapper);
        } else {
            publish(event);
        }
    }
    
    /**
     * 异步领域事件包装器
     */
    public static class AsyncDomainEventWrapper {
        private final DomainEvent domainEvent;
        
        public AsyncDomainEventWrapper(DomainEvent domainEvent) {
            this.domainEvent = domainEvent;
        }
        
        public DomainEvent getDomainEvent() {
            return domainEvent;
        }
    }
} 