package com.example.springai.research.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient Configuration
 * 
 * Configures the ChatClient bean which is the core API for AI interactions.
 * The ChatClient.Builder is auto-configured by Spring Boot based on 
 * the AI provider settings in application.yml.
 */
@Configuration
public class ChatClientConfig {

    /**
     * Creates a ChatClient bean using the auto-configured builder.
     * The builder will use the default AI provider configured in application.yml
     * (OpenAI, Anthropic, or Ollama based on availability).
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a helpful AI assistant powered by Spring AI 1.0")
                .build();
    }
}
