package com.example.springai.core.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Configuration for AI models and chat clients
 */
@Configuration
public class AiConfiguration {

    /**
     * Primary ChatClient with memory and safety advisors
     */
    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                    new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                    new SafeGuardAdvisor(List.of("violence", "hate speech", "illegal content"))
                );
    }

    /**
     * OpenAI specific ChatClient
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.openai.api-key")
    public ChatClient openAiChatClient(@Autowired OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("You are a helpful AI assistant powered by OpenAI.")
                .build();
    }

    /**
     * Anthropic specific ChatClient
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.anthropic.api-key")
    public ChatClient anthropicChatClient(@Autowired AnthropicChatModel anthropicChatModel) {
        return ChatClient.builder(anthropicChatModel)
                .defaultSystem("You are Claude, a helpful AI assistant created by Anthropic.")
                .build();
    }

    /**
     * Azure OpenAI specific ChatClient
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.azure.openai.api-key")
    public ChatClient azureOpenAiChatClient(@Autowired AzureOpenAiChatModel azureOpenAiChatModel) {
        return ChatClient.builder(azureOpenAiChatModel)
                .defaultSystem("You are a helpful AI assistant powered by Azure OpenAI.")
                .build();
    }

    /**
     * Ollama specific ChatClient for local models
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.ollama.base-url")
    public ChatClient ollamaChatClient(@Autowired(required = false) OllamaChatModel ollamaChatModel) {
        if (ollamaChatModel == null) {
            return null;
        }
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("You are a helpful AI assistant running locally via Ollama.")
                .build();
    }

    /**
     * ChatClient with custom prompt engineering
     */
    @Bean
    public ChatClient promptEngineeringChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    You are an expert AI assistant with the following capabilities:
                    1. You provide accurate, detailed, and well-structured responses
                    2. You use examples and analogies when explaining complex concepts
                    3. You acknowledge uncertainty when you're not sure about something
                    4. You format your responses using markdown for better readability
                    5. You're concise but thorough in your explanations
                    """)
                .build();
    }

    /**
     * ChatClient with conversation memory
     */
    @Bean
    public ChatClient conversationChatClient(ChatModel chatModel) {
        var chatMemory = new InMemoryChatMemory();
        
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                    new PromptChatMemoryAdvisor(chatMemory),
                    new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
}
