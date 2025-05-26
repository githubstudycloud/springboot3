package com.example.springai.research.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * Person Profile Model
 * 
 * Simple example for structured output of person information.
 */
@Data
public class PersonProfile {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("age")
    private Integer age;
    
    @JsonProperty("occupation")
    private String occupation;
    
    @JsonProperty("skills")
    private List<String> skills;
    
    @JsonProperty("bio")
    private String bio;
}
