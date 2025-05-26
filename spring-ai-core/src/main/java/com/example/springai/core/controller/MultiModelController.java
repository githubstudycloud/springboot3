package com.example.springai.core.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller demonstrating multi-model capabilities
 */
@RestController
@RequestMapping("/api/multi-model")
public class MultiModelController {

    private final Map<String, ChatClient> chatClients = new HashMap<>();

    @Autowired
    public MultiModelController(
            @Qualifier("openAiChatClient") ChatClient openAiChatClient,
            @Qualifier("anthropicChatClient") ChatClient anthropicChatClient,
            @Qualifier("azureOpenAiChatClient") ChatClient azureOpenAiChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        
        if (openAiChatClient != null) {
            chatClients.put("openai", openAiChatClient);
        }
        if (anthropicChatClient != null) {
            chatClients.put("anthropic", anthropicChatClient);
        }
        if (azureOpenAiChatClient != null) {
            chatClients.put("azure", azureOpenAiChatClient);
        }
        if (ollamaChatClient != null) {
            chatClients.put("ollama", ollamaChatClient);
        }
    }

    /**
     * Chat with specific model
     */
    @PostMapping("/chat/{model}")
    public ModelResponse chatWithModel(@PathVariable String model, @RequestBody String message) {
        ChatClient client = chatClients.get(model.toLowerCase());
        
        if (client == null) {
            return new ModelResponse(model, "Model not available: " + model, null);
        }

        try {
            long startTime = System.currentTimeMillis();
            String response = client.prompt()
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();
            
            return new ModelResponse(model, response, endTime - startTime);
        } catch (Exception e) {
            return new ModelResponse(model, "Error: " + e.getMessage(), null);
        }
    }

    /**
     * Compare responses from multiple models
     */
    @PostMapping("/compare")
    public Map<String, ModelResponse> compareModels(@RequestBody String message) {
        Map<String, ModelResponse> responses = new HashMap<>();
        
        for (Map.Entry<String, ChatClient> entry : chatClients.entrySet()) {
            String model = entry.getKey();
            ChatClient client = entry.getValue();
            
            try {
                long startTime = System.currentTimeMillis();
                String response = client.prompt()
                        .user(message)
                        .call()
                        .content();
                long endTime = System.currentTimeMillis();
                
                responses.put(model, new ModelResponse(model, response, endTime - startTime));
            } catch (Exception e) {
                responses.put(model, new ModelResponse(model, "Error: " + e.getMessage(), null));
            }
        }
        
        return responses;
    }

    /**
     * Get available models
     */
    @GetMapping("/available")
    public Map<String, Boolean> getAvailableModels() {
        Map<String, Boolean> availability = new HashMap<>();
        availability.put("openai", chatClients.containsKey("openai"));
        availability.put("anthropic", chatClients.containsKey("anthropic"));
        availability.put("azure", chatClients.containsKey("azure"));
        availability.put("ollama", chatClients.containsKey("ollama"));
        return availability;
    }

    /**
     * Model-specific features demo
     */
    @PostMapping("/features/{model}")
    public Map<String, Object> demonstrateModelFeatures(@PathVariable String model, @RequestBody String message) {
        ChatClient client = chatClients.get(model.toLowerCase());
        
        if (client == null) {
            return Map.of("error", "Model not available: " + model);
        }

        Map<String, Object> features = new HashMap<>();
        
        // Basic response
        features.put("basic_response", client.prompt()
                .user(message)
                .call()
                .content());
        
        // With temperature
        features.put("creative_response", client.prompt()
                .user(message)
                .options(Map.of("temperature", 0.9))
                .call()
                .content());
        
        // With max tokens
        features.put("concise_response", client.prompt()
                .user(message)
                .options(Map.of("maxTokens", 50))
                .call()
                .content());
        
        return features;
    }

    public record ModelResponse(String model, String response, Long responseTimeMs) {}
}
