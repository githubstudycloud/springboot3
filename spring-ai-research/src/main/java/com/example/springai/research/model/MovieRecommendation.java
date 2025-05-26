package com.example.springai.research.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * Movie Recommendation Model
 * 
 * Example POJO for demonstrating structured output.
 * The AI response will be automatically mapped to this structure.
 */
@Data
public class MovieRecommendation {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("year")
    private Integer year;
    
    @JsonProperty("genre")
    private List<String> genre;
    
    @JsonProperty("director")
    private String director;
    
    @JsonProperty("plot_summary")
    private String plotSummary;
    
    @JsonProperty("rating")
    private Double rating;
    
    @JsonProperty("reasons_to_watch")
    private List<String> reasonsToWatch;
    
    @JsonProperty("similar_movies")
    private List<String> similarMovies;
}
