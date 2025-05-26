package com.example.springai.tools.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating tool/function calling capabilities
 */
@RestController
@RequestMapping("/api/tools")
public class ToolsController {

    private final ChatClient chatClientWithTools;

    @Autowired
    public ToolsController(@Qualifier("chatClientWithTools") ChatClient chatClientWithTools) {
        this.chatClientWithTools = chatClientWithTools;
    }

    /**
     * Chat with tool calling capability
     */
    @PostMapping("/chat")
    public ToolResponse chatWithTools(@RequestBody String message) {
        var response = chatClientWithTools.prompt()
                .user(message)
                .call()
                .chatResponse();
        
        return new ToolResponse(
            response.getResult().getOutput().getContent(),
            extractToolCalls(response),
            response.getMetadata()
        );
    }

    /**
     * Demo: Weather assistant
     */
    @PostMapping("/weather-assistant")
    public String weatherAssistant(@RequestParam String query) {
        String systemPrompt = """
            You are a helpful weather assistant. When users ask about weather,
            use the getCurrentWeather function to get real-time weather data.
            Always provide the temperature, conditions, and humidity in a friendly way.
            """;
        
        return chatClientWithTools.prompt()
                .system(systemPrompt)
                .user(query)
                .call()
                .content();
    }

    /**
     * Demo: Database assistant
     */
    @PostMapping("/database-assistant")
    public String databaseAssistant(@RequestParam String query) {
        String systemPrompt = """
            You are a database assistant. Help users query the database using the queryDatabase function.
            Available tables: users, products, orders.
            Always explain what data you're retrieving and present it in a clear format.
            """;
        
        return chatClientWithTools.prompt()
                .system(systemPrompt)
                .user(query)
                .call()
                .content();
    }

    /**
     * Demo: Calculator assistant
     */
    @PostMapping("/calculator-assistant")
    public String calculatorAssistant(@RequestParam String query) {
        String systemPrompt = """
            You are a mathematical assistant. Use the calculate function to solve math problems.
            Show your work step by step and explain the calculations.
            """;
        
        return chatClientWithTools.prompt()
                .system(systemPrompt)
                .user(query)
                .call()
                .content();
    }

    /**
     * Demo: Multi-tool assistant
     */
    @PostMapping("/multi-tool-assistant")
    public String multiToolAssistant(@RequestBody MultiToolRequest request) {
        String systemPrompt = """
            You are a versatile AI assistant with access to multiple tools:
            1. Weather information
            2. Database queries
            3. Mathematical calculations
            4. Email sending
            5. Web search
            
            Use the appropriate tools to help the user with their request.
            You can use multiple tools in sequence if needed.
            """;
        
        return chatClientWithTools.prompt()
                .system(systemPrompt)
                .user(request.query())
                .call()
                .content();
    }

    /**
     * Demo: Complex workflow
     */
    @PostMapping("/workflow")
    public String executeWorkflow(@RequestBody WorkflowRequest request) {
        String systemPrompt = """
            You are an AI assistant that can execute complex workflows.
            Break down the user's request into steps and use the appropriate tools.
            Provide a summary of each step and the final result.
            """;
        
        String userPrompt = String.format("""
            Execute the following workflow:
            %s
            
            Context: %s
            """, request.workflow(), request.context());
        
        return chatClientWithTools.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     * Get available tools
     */
    @GetMapping("/available")
    public List<ToolInfo> getAvailableTools() {
        return List.of(
            new ToolInfo("getCurrentWeather", "Get current weather for a location", 
                List.of("location", "unit")),
            new ToolInfo("queryDatabase", "Query the database", 
                List.of("table", "filters", "limit")),
            new ToolInfo("calculate", "Perform mathematical calculations", 
                List.of("operation", "a", "b")),
            new ToolInfo("sendEmail", "Send an email", 
                List.of("to", "subject", "body")),
            new ToolInfo("searchWeb", "Search the web", 
                List.of("query", "limit"))
        );
    }

    private List<Map<String, Object>> extractToolCalls(ChatResponse response) {
        // Extract tool call information from the response
        // This is a simplified version - actual implementation would parse the response properly
        return List.of();
    }

    // DTOs
    public record ToolResponse(String response, List<Map<String, Object>> toolCalls, Map<String, Object> metadata) {}
    public record MultiToolRequest(String query, Map<String, String> preferences) {}
    public record WorkflowRequest(String workflow, Map<String, Object> context) {}
    public record ToolInfo(String name, String description, List<String> parameters) {}
}
