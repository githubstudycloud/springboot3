package com.example.springai.tools.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Function;

/**
 * Function to get weather information
 */
public class WeatherFunction implements Function<WeatherFunction.Request, WeatherFunction.Response> {

    private final WebClient webClient;

    public WeatherFunction(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Response apply(Request request) {
        // Simulate weather API call
        // In real implementation, this would call an actual weather API
        double temperature = Math.random() * 30 + 10; // Random temp between 10-40
        String condition = Math.random() > 0.5 ? "Sunny" : "Cloudy";
        int humidity = (int) (Math.random() * 50 + 30); // Random humidity 30-80%
        
        return new Response(
            request.location(),
            temperature,
            condition,
            humidity,
            "Celsius"
        );
    }

    public record Request(
        @JsonProperty("location") String location,
        @JsonProperty("unit") String unit
    ) {}

    public record Response(
        @JsonProperty("location") String location,
        @JsonProperty("temperature") double temperature,
        @JsonProperty("condition") String condition,
        @JsonProperty("humidity") int humidity,
        @JsonProperty("unit") String unit
    ) {}
}
