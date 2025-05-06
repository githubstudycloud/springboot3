package com.platform.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 动态路由配置
 * <p>
 * 支持动态刷新路由配置，用于运行时更新路由规则
 * </p>
 */
@Component
@RefreshScope
public class DynamicRouteConfig implements ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(DynamicRouteConfig.class);
    
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
    
    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;
    
    private ApplicationEventPublisher publisher;
    
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
    
    /**
     * 添加路由
     *
     * @param definition 路由定义
     * @return 操作结果
     */
    public String addRoute(RouteDefinition definition) {
        log.info("Gateway add route: {}", definition.getId());
        
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            log.error("Gateway add route error: {}", e.getMessage(), e);
            return "fail: " + e.getMessage();
        }
    }
    
    /**
     * 更新路由
     *
     * @param definition 路由定义
     * @return 操作结果
     */
    public String updateRoute(RouteDefinition definition) {
        log.info("Gateway update route: {}", definition.getId());
        
        try {
            deleteRoute(definition.getId());
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            log.error("Gateway update route error: {}", e.getMessage(), e);
            return "fail: " + e.getMessage();
        }
    }
    
    /**
     * 删除路由
     *
     * @param id 路由ID
     * @return 操作结果
     */
    public String deleteRoute(String id) {
        log.info("Gateway delete route: {}", id);
        
        try {
            routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            log.error("Gateway delete route error: {}", e.getMessage(), e);
            return "fail: " + e.getMessage();
        }
    }
}
