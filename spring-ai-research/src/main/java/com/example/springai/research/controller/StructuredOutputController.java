package com.example.springai.research.controller;

import com.example.springai.research.model.MovieRecommendation;
import com.example.springai.research.model.PersonProfile;
import com.example.springai.research.model.TechnicalAnalysis;
import com.example.springai.research.service.StructuredOutputService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Structured Output Controller
 * 
 * REST endpoints demonstrating Spring AI's structured output capabilities.
 * Shows how AI responses can be automatically mapped to POJOs.
 */
@RestController
@RequestMapping("/api/structured")
@RequiredArgsConstructor
@Slf4j
public class StructuredOutputController {
    
    private final StructuredOutputService structuredOutputService;
    
    /**
     * Get a movie recommendation as structured data
     */
    @GetMapping("/movie/{genre}")
    public MovieRecommendation getMovieRecommendation(@PathVariable String genre) {
        log.info("Getting movie recommendation for genre: {}", genre);
        return structuredOutputService.getMovieRecommendation(genre);
    }
    
    /**
     * Get multiple movie recommendations
     */
    @GetMapping("/movies/{genre}")
    public List<MovieRecommendation> getMovieRecommendations(
            @PathVariable String genre,
            @RequestParam(defaultValue = "3") int count) {
        log.info("Getting {} movie recommendations for genre: {}", count, genre);
        return structuredOutputService.getMovieRecommendations(genre, count);
    }
    
    /**
     * Generate a person profile
     */
    @PostMapping("/person")
    public PersonProfile generatePersonProfile(@RequestBody ProfileRequest request) {
        log.info("Generating person profile");
        return structuredOutputService.generatePersonProfile(request.description());
    }
    
    /**
     * Analyze a technical topic
     */
    @PostMapping("/analyze")
    public TechnicalAnalysis analyzeTopic(@RequestBody AnalysisRequest request) {
        log.info("Analyzing topic: {}", request.topic());
        return structuredOutputService.analyzeTopic(request.topic());
    }
    
    /**
     * Custom structured output endpoint
     */
    @PostMapping("/custom")
    public Map<String, Object> customStructuredOutput(@RequestBody CustomRequest request) {
        log.info("Processing custom structured output");
        
        // Example of dynamic structured output
        record CustomOutput(String analysis, List<String> keyPoints, double confidence) {}
        
        CustomOutput result = structuredOutputService.getStructuredOutput(
            request.prompt(), 
            CustomOutput.class
        );
        
        return Map.of(
            "result", result,
            "type", "CustomOutput"
        );
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "ok",
            "service", "StructuredOutput",
            "version", "1.0.0"
        );
    }
    
    /**
     * Request records
     */
    public record ProfileRequest(String description) {}
    public record AnalysisRequest(String topic) {}
    public record CustomRequest(String prompt) {}
}
