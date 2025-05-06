package com.platform.gateway.controller;

import com.platform.gateway.config.DynamicRouteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关管理控制器
 * <p>
 * 提供网关路由配置的管理接口
 * </p>
 */
@RestController
@RequestMapping("/admin/gateway")
public class GatewayAdminController {

    private static final Logger log = LoggerFactory.getLogger(GatewayAdminController.class);
    
    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;
    
    @Autowired
    private DynamicRouteConfig dynamicRouteConfig;
    
    /**
     * 获取所有路由定义
     *
     * @return 路由定义列表
     */
    @GetMapping("/routes")
    public Mono<ResponseEntity<List<RouteDefinition>>> getRoutes() {
        Flux<RouteDefinition> routeDefinitionFlux = routeDefinitionLocator.getRouteDefinitions();
        return routeDefinitionFlux.collectList()
                .map(definitions -> ResponseEntity.ok().body(definitions));
    }
    
    /**
     * 添加新路由
     *
     * @param routeDefinition 路由定义
     * @return 操作结果
     */
    @PostMapping("/route")
    public Mono<ResponseEntity<String>> addRoute(@RequestBody RouteDefinition routeDefinition) {
        String result = dynamicRouteConfig.addRoute(routeDefinition);
        return Mono.just(ResponseEntity.ok().body(result));
    }
    
    /**
     * 更新路由
     *
     * @param routeDefinition 路由定义
     * @return 操作结果
     */
    @PutMapping("/route")
    public Mono<ResponseEntity<String>> updateRoute(@RequestBody RouteDefinition routeDefinition) {
        String result = dynamicRouteConfig.updateRoute(routeDefinition);
        return Mono.just(ResponseEntity.ok().body(result));
    }
    
    /**
     * 删除路由
     *
     * @param id 路由ID
     * @return 操作结果
     */
    @DeleteMapping("/route/{id}")
    public Mono<ResponseEntity<String>> deleteRoute(@PathVariable String id) {
        String result = dynamicRouteConfig.deleteRoute(id);
        return Mono.just(ResponseEntity.ok().body(result));
    }
    
    /**
     * 创建简单路由
     * <p>
     * 提供简化的接口创建基本路由
     * </p>
     *
     * @param serviceId 服务ID
     * @param path 路径模式
     * @param stripPrefix 是否剥离前缀
     * @return 操作结果
     */
    @PostMapping("/route/simple")
    public Mono<ResponseEntity<String>> createSimpleRoute(
            @RequestParam String serviceId,
            @RequestParam String path,
            @RequestParam(defaultValue = "true") boolean stripPrefix) {
        
        try {
            // 构建路由定义
            RouteDefinition definition = new RouteDefinition();
            definition.setId(serviceId);
            definition.setUri(URI.create("lb://" + serviceId));
            
            // 添加路径断言
            List<PredicateDefinition> predicates = new ArrayList<>();
            PredicateDefinition pathPredicate = new PredicateDefinition();
            pathPredicate.setName("Path");
            Map<String, String> predicateParams = new HashMap<>(1);
            predicateParams.put("pattern", path);
            pathPredicate.setArgs(predicateParams);
            predicates.add(pathPredicate);
            definition.setPredicates(predicates);
            
            // 添加过滤器
            if (stripPrefix) {
                definition.getFilters().add(filterDefinition("StripPrefix", "parts", "1"));
            }
            
            String result = dynamicRouteConfig.addRoute(definition);
            return Mono.just(ResponseEntity.ok().body(result));
        } catch (Exception e) {
            log.error("Create simple route error: {}", e.getMessage(), e);
            return Mono.just(ResponseEntity.badRequest().body("创建路由失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建过滤器定义
     *
     * @param name 过滤器名称
     * @param paramKey 参数键
     * @param paramValue 参数值
     * @return 过滤器定义
     */
    private org.springframework.cloud.gateway.filter.FilterDefinition filterDefinition(
            String name, String paramKey, String paramValue) {
        org.springframework.cloud.gateway.filter.FilterDefinition filter = 
                new org.springframework.cloud.gateway.filter.FilterDefinition();
        filter.setName(name);
        Map<String, String> filterParams = new HashMap<>(1);
        filterParams.put(paramKey, paramValue);
        filter.setArgs(filterParams);
        return filter;
    }
}
