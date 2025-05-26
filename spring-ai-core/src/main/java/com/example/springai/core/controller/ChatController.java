package com.example.springai.core.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Controller demonstrating basic chat functionality with different AI providers
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    @Autowired
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Simple chat endpoint
     */
    @PostMapping("/simple")
    public String simpleChat(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * Chat with template
     */
    @PostMapping("/template")
    public String templateChat(@RequestParam String topic, @RequestParam String style) {
        PromptTemplate promptTemplate = new PromptTemplate(
            "Generate a {style} explanation about {topic}. Keep it concise and informative."
        );
        
        return chatClient.prompt()
                .user(promptTemplate.createMessage(Map.of("style", style, "topic", topic)))
                .call()
                .content();
    }

    /**
     * Streaming chat endpoint
     */
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> streamChat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    /**
     * Chat with system message
     */
    @PostMapping("/system")
    public String chatWithSystem(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .system(request.systemMessage())
                .user(request.userMessage())
                .call()
                .content();
    }

    /**
     * Multi-turn conversation
     */
    @PostMapping("/conversation")
    public String conversationChat(@RequestBody ConversationRequest request) {
        var prompt = chatClient.prompt();
        
        // Add conversation history
        for (var message : request.history()) {
            if ("user".equals(message.role())) {
                prompt.user(message.content());
            } else if ("assistant".equals(message.role())) {
                prompt.assistant(message.content());
            }
        }
        
        // Add new user message
        prompt.user(request.newMessage());
        
        return prompt.call().content();
    }

    // Request DTOs
    public record ChatRequest(String systemMessage, String userMessage) {}
    public record ConversationRequest(List<Message> history, String newMessage) {}
    public record Message(String role, String content) {}
}
