package com.example.springai.core.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating structured output capabilities
 */
@RestController
@RequestMapping("/api/structured")
public class StructuredOutputController {

    private final ChatClient chatClient;

    @Autowired
    public StructuredOutputController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Extract structured data as POJO
     */
    @PostMapping("/extract-person")
    public Person extractPerson(@RequestBody String text) {
        var outputConverter = new BeanOutputConverter<>(Person.class);
        
        String prompt = """
            Extract person information from the following text.
            Return the result in the following JSON format: %s
            
            Text: %s
            """.formatted(outputConverter.getFormat(), text);
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        return outputConverter.convert(response);
    }

    /**
     * Extract list of items
     */
    @PostMapping("/extract-list")
    public List<Product> extractProducts(@RequestBody String text) {
        var outputConverter = new ListOutputConverter<>(new BeanOutputConverter<>(Product.class));
        
        String prompt = """
            Extract all products mentioned in the following text.
            Return the result as a JSON array in the following format: %s
            
            Text: %s
            """.formatted(outputConverter.getFormat(), text);
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        return outputConverter.convert(response);
    }

    /**
     * Generate structured content
     */
    @PostMapping("/generate-recipe")
    public Recipe generateRecipe(@RequestParam String ingredients) {
        var outputConverter = new BeanOutputConverter<>(Recipe.class);
        
        String prompt = """
            Create a recipe using the following ingredients: %s
            Return the result in the following JSON format: %s
            """.formatted(ingredients, outputConverter.getFormat());
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        return outputConverter.convert(response);
    }

    /**
     * Analyze sentiment with structured output
     */
    @PostMapping("/analyze-sentiment")
    public SentimentAnalysis analyzeSentiment(@RequestBody String text) {
        var outputConverter = new BeanOutputConverter<>(SentimentAnalysis.class);
        
        String prompt = """
            Analyze the sentiment of the following text.
            Return the result in the following JSON format: %s
            
            Text: %s
            """.formatted(outputConverter.getFormat(), text);
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        return outputConverter.convert(response);
    }

    // Data classes
    public record Person(
        @JsonProperty("name") String name,
        @JsonProperty("age") Integer age,
        @JsonProperty("email") String email,
        @JsonProperty("occupation") String occupation
    ) {}

    public record Product(
        @JsonProperty("name") String name,
        @JsonProperty("price") Double price,
        @JsonProperty("description") String description,
        @JsonProperty("category") String category
    ) {}

    public record Recipe(
        @JsonProperty("name") String name,
        @JsonProperty("servings") Integer servings,
        @JsonProperty("prepTime") String prepTime,
        @JsonProperty("cookTime") String cookTime,
        @JsonProperty("ingredients") List<Ingredient> ingredients,
        @JsonProperty("instructions") List<String> instructions,
        @JsonProperty("nutritionInfo") NutritionInfo nutritionInfo
    ) {}

    public record Ingredient(
        @JsonProperty("item") String item,
        @JsonProperty("amount") String amount
    ) {}

    public record NutritionInfo(
        @JsonProperty("calories") Integer calories,
        @JsonProperty("protein") String protein,
        @JsonProperty("fat") String fat,
        @JsonProperty("carbs") String carbs
    ) {}

    public record SentimentAnalysis(
        @JsonProperty("sentiment") String sentiment,
        @JsonProperty("score") Double score,
        @JsonProperty("emotions") Map<String, Double> emotions,
        @JsonProperty("summary") String summary
    ) {}
}
