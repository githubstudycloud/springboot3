package com.example.springai.research.controller;

import com.example.springai.research.service.ChatService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Chat Controller
 * 
 * REST endpoints demonstrating ChatClient capabilities.
 * Provides various chat interaction patterns including simple chat,
 * templated prompts, and metadata-rich responses.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * Simple chat endpoint
     * 
     * @param message User input message
     * @return AI response as plain text
     */
    @PostMapping(value = "/simple", produces = MediaType.TEXT_PLAIN_VALUE)
    public String simpleChat(@RequestBody String message) {
        log.info("Simple chat request received");
        return chatService.chat(message);
    }

    /**
     * Chat with custom system prompt
     * 
     * @param request Contains user message and system prompt
     * @return AI response as plain text
     */
    @PostMapping(value = "/system", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chatWithSystem(@RequestBody ChatRequest request) {
        log.info("Chat with system prompt request received");
        return chatService.chatWithSystem(request.message(), request.systemPrompt());
    }

    /**
     * Templated chat with parameters
     * 
     * @param request Contains template and parameters
     * @return AI response as plain text
     */
    @PostMapping(value = "/template", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chatWithTemplate(@RequestBody TemplateRequest request) {
        log.info("Templated chat request received");
        return chatService.chatWithTemplate(request.template(), request.params());
    }

    /**
     * Chat with full response metadata
     * 
     * @param message User input message
     * @return Full chat response including metadata
     */
    @PostMapping("/metadata")
    public ChatResponse chatWithMetadata(@RequestBody String message) {
        log.info("Chat with metadata request received");
        return chatService.chatWithMetadata(message);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "ok",
            "service", "ChatClient",
            "version", "1.0.0"
        );
    }

    /**
     * Request record for chat with system prompt
     */
    public record ChatRequest(String message, String systemPrompt) {}

    /**
     * Request record for templated chat
     */
    public record TemplateRequest(String template, Map<String, Object> params) {}
}
