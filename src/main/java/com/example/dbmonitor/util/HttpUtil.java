package com.example.dbmonitor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
public class HttpUtil {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public HttpUtil() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }
    
    public void sendAlert(String url, Object data) throws HttpTimeoutException, InterruptedException {
        try {
            String jsonBody = serializeToJson(data);
            HttpRequest request = buildPostRequest(url, jsonBody);
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.debug("Alert sent successfully to {}: status={}", url, response.statusCode());
            } else {
                log.error("Alert request failed. URL: {}, Status: {}, Body: {}", 
                    url, response.statusCode(), response.body());
                // 可以根据需要抛出自定义异常
            }
        } catch (ConnectException e) {
            log.error("Connection failed to alert endpoint: {}", url, e);
            throw new HttpTimeoutException("Failed to connect to: " + url);
        } catch (IOException e) {
            log.error("IO error sending alert to: {}", url, e);
            throw new HttpTimeoutException("IO error: " + e.getMessage());
        }
    }
    
    public CompletableFuture<Void> sendAlertAsync(String url, Object data) {
        try {
            String jsonBody = serializeToJson(data);
            HttpRequest request = buildPostRequest(url, jsonBody);
            
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .handle((response, throwable) -> {
                        if (throwable != null) {
                            handleAsyncError(url, throwable);
                            return null;
                        }
                        
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            log.debug("Alert sent successfully to {}: status={}", url, response.statusCode());
                        } else {
                            log.error("Alert request failed. URL: {}, Status: {}, Body: {}", 
                                url, response.statusCode(), response.body());
                        }
                        return null;
                    });
        } catch (IllegalArgumentException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private String serializeToJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            throw new IllegalArgumentException("Cannot serialize data to JSON", e);
        }
    }
    
    private HttpRequest buildPostRequest(String url, String jsonBody) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "DB-Monitor/1.0")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
        } catch (URISyntaxException e) {
            log.error("Invalid URL: {}", url, e);
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
    
    private void handleAsyncError(String url, Throwable throwable) {
        if (throwable instanceof CompletionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        
        if (throwable instanceof HttpTimeoutException) {
            log.error("Request timeout for URL: {}", url, throwable);
        } else if (throwable instanceof ConnectException) {
            log.error("Connection failed to URL: {}", url, throwable);
        } else if (throwable instanceof IOException) {
            log.error("IO error for URL: {}", url, throwable);
        } else {
            log.error("Unexpected error for URL: {}", url, throwable);
        }
    }
}