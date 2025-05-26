package com.example.springai.research.service;

import com.example.springai.research.model.MovieRecommendation;
import com.example.springai.research.model.PersonProfile;
import com.example.springai.research.model.TechnicalAnalysis;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Structured Output Service
 * 
 * Demonstrates Spring AI's capability to map AI responses to Java objects.
 * Uses BeanOutputConverter for automatic JSON to POJO conversion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StructuredOutputService {
    
    private final ChatClient chatClient;
    
    /**
     * Get movie recommendation as a structured object
     */
    public MovieRecommendation getMovieRecommendation(String genre) {
        log.debug("Getting movie recommendation for genre: {}", genre);
        
        return chatClient.prompt()
                .user(u -> u.text("Recommend a {genre} movie")
                        .param("genre", genre))
                .call()
                .entity(MovieRecommendation.class);
    }
    
    /**
     * Get multiple movie recommendations
     */
    public List<MovieRecommendation> getMovieRecommendations(String genre, int count) {
        log.debug("Getting {} movie recommendations for genre: {}", count, genre);
        
        var converter = new BeanOutputConverter<>(MovieRecommendation.class);
        
        String prompt = String.format(
            "Recommend %d different %s movies. %s", 
            count, genre, converter.getFormat()
        );
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        // For multiple items, we need to handle the JSON array
        // This is a simplified example - in production, use proper JSON parsing
        return List.of(converter.convert(response));
    }
    
    /**
     * Generate a person profile
     */
    public PersonProfile generatePersonProfile(String description) {
        log.debug("Generating person profile for: {}", description);
        
        return chatClient.prompt()
                .user(u -> u.text("Create a profile for a {description}")
                        .param("description", description))
                .call()
                .entity(PersonProfile.class);
    }
    
    /**
     * Analyze a technical topic
     */
    public TechnicalAnalysis analyzeTopic(String topic) {
        log.debug("Analyzing technical topic: {}", topic);
        
        var converter = new BeanOutputConverter<>(TechnicalAnalysis.class);
        
        String prompt = String.format(
            "Provide a technical analysis of %s. Include key concepts, pros/cons, and use cases. %s",
            topic, converter.getFormat()
        );
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        return converter.convert(response);
    }
    
    /**
     * Generic structured output for any class
     */
    public <T> T getStructuredOutput(String prompt, Class<T> targetClass) {
        log.debug("Getting structured output for class: {}", targetClass.getSimpleName());
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(targetClass);
    }
    
    /**
     * Get structured output with custom format instructions
     */
    public <T> T getStructuredOutputWithFormat(String prompt, Class<T> targetClass) {
        log.debug("Getting structured output with format for class: {}", targetClass.getSimpleName());
        
        var converter = new BeanOutputConverter<>(targetClass);
        String fullPrompt = prompt + "\n" + converter.getFormat();
        
        String response = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();
        
        return converter.convert(response);
    }
}
