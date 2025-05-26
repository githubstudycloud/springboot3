package com.example.springai.research.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Chat Service
 * 
 * Provides various chat interaction patterns using the ChatClient API.
 * Demonstrates synchronous, streaming, and parameterized chat capabilities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatClient chatClient;

    /**
     * Simple synchronous chat interaction
     */
    public String chat(String userInput) {
        log.debug("Processing chat request: {}", userInput);
        
        return chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }

    /**
     * Chat with custom system prompt
     */
    public String chatWithSystem(String userInput, String systemPrompt) {
        log.debug("Processing chat with system prompt");
        
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userInput)
                .call()
                .content();
    }

    /**
     * Chat with parameters for template-based prompts
     */
    public String chatWithTemplate(String template, Map<String, Object> params) {
        log.debug("Processing templated chat with params: {}", params);
        
        var promptBuilder = chatClient.prompt();
        
        // Apply parameters to the prompt
        params.forEach(promptBuilder::param);
        
        return promptBuilder
                .user(template)
                .call()
                .content();
    }

    /**
     * Get full chat response with metadata
     */
    public ChatResponse chatWithMetadata(String userInput) {
        log.debug("Processing chat request with metadata");
        
        return chatClient.prompt()
                .user(userInput)
                .call()
                .chatResponse();
    }

    /**
     * Multi-turn conversation with message history
     */
    public String chatWithHistory(List<Message> messages, String newUserInput) {
        log.debug("Processing chat with history, {} previous messages", messages.size());
        
        var promptBuilder = chatClient.prompt();
        
        // Add historical messages
        messages.forEach(promptBuilder::messages);
        
        // Add new user input
        promptBuilder.user(newUserInput);
        
        return promptBuilder
                .call()
                .content();
    }
}
