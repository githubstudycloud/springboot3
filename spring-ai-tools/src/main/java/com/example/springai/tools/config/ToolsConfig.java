package com.example.springai.tools.config;

import com.example.springai.tools.functions.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Configuration for AI tools and functions
 */
@Configuration
public class ToolsConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public WeatherFunction weatherFunction(WebClient webClient) {
        return new WeatherFunction(webClient);
    }

    @Bean
    public DatabaseFunction databaseFunction() {
        return new DatabaseFunction();
    }

    @Bean
    public EmailFunction emailFunction() {
        return new EmailFunction();
    }

    @Bean
    public CalculatorFunction calculatorFunction() {
        return new CalculatorFunction();
    }

    @Bean
    public SearchFunction searchFunction(WebClient webClient) {
        return new SearchFunction(webClient);
    }

    @Bean
    public ChatClient chatClientWithTools(ChatModel chatModel,
                                         WeatherFunction weatherFunction,
                                         DatabaseFunction databaseFunction,
                                         EmailFunction emailFunction,
                                         CalculatorFunction calculatorFunction,
                                         SearchFunction searchFunction) {
        
        // Create function callbacks
        FunctionCallback weatherCallback = FunctionCallbackWrapper.builder(weatherFunction)
                .withName("getCurrentWeather")
                .withDescription("Get the current weather for a given location")
                .build();

        FunctionCallback databaseCallback = FunctionCallbackWrapper.builder(databaseFunction)
                .withName("queryDatabase")
                .withDescription("Query the database for information")
                .build();

        FunctionCallback emailCallback = FunctionCallbackWrapper.builder(emailFunction)
                .withName("sendEmail")
                .withDescription("Send an email to a recipient")
                .build();

        FunctionCallback calculatorCallback = FunctionCallbackWrapper.builder(calculatorFunction)
                .withName("calculate")
                .withDescription("Perform mathematical calculations")
                .build();

        FunctionCallback searchCallback = FunctionCallbackWrapper.builder(searchFunction)
                .withName("searchWeb")
                .withDescription("Search the web for information")
                .build();

        return ChatClient.builder(chatModel)
                .defaultFunctions(
                    weatherCallback,
                    databaseCallback,
                    emailCallback,
                    calculatorCallback,
                    searchCallback
                )
                .build();
    }
}
