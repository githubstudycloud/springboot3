package com.example.springai.research.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Structured Output Configuration
 * 
 * Configures JSON parsing and formatting for structured outputs.
 */
@Configuration
public class StructuredOutputConfig {
    
    /**
     * Configure ObjectMapper for pretty printing and better date handling
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.findAndRegisterModules();
        return mapper;
    }
}
