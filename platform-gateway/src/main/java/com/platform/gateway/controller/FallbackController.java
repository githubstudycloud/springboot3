package com.platform.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller to handle fallback responses when services are unavailable.
 * 
 * Used by the circuit breaker when downstream services fail.
 */
@RestController
@Slf4j
public class FallbackController {

    /**
     * Default fallback endpoint.
     * 
     * @param exchange the current server exchange
     * @return a standard fallback response
     */
    @RequestMapping("/fallback")
    public Mono<ResponseEntity<Map<String, Object>>> fallback(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");
        
        log.warn("Fallback triggered for request: path={}, method={}, requestId={}", 
                path, method, requestId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", "The service is currently unavailable. Please try again later.");
        response.put("path", path);
        if (requestId != null) {
            response.put("requestId", requestId);
        }
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
    
    /**
     * Health check endpoint.
     * 
     * @return a simple response indicating the gateway is up
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "platform-gateway");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return Mono.just(ResponseEntity.ok(response));
    }
}
