package com.example.springai.research.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Technical Analysis Model
 * 
 * Demonstrates more complex structured output with nested objects.
 */
@Data
public class TechnicalAnalysis {
    
    @JsonProperty("topic")
    private String topic;
    
    @JsonProperty("complexity_level")
    private String complexityLevel;
    
    @JsonProperty("summary")
    private String summary;
    
    @JsonProperty("key_concepts")
    private List<Concept> keyConcepts;
    
    @JsonProperty("pros_and_cons")
    private ProsCons prosAndCons;
    
    @JsonProperty("use_cases")
    private List<String> useCases;
    
    @JsonProperty("recommendations")
    private Map<String, String> recommendations;
    
    @Data
    public static class Concept {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("importance")
        private String importance;
    }
    
    @Data
    public static class ProsCons {
        @JsonProperty("pros")
        private List<String> pros;
        
        @JsonProperty("cons")
        private List<String> cons;
    }
}
