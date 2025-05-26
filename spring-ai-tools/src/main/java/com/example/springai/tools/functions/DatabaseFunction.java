package com.example.springai.tools.functions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.function.Function;

/**
 * Function to query database
 */
public class DatabaseFunction implements Function<DatabaseFunction.Request, DatabaseFunction.Response> {

    // Simulated database
    private final Map<String, List<Map<String, Object>>> database = new HashMap<>();

    public DatabaseFunction() {
        // Initialize with sample data
        database.put("users", List.of(
            Map.of("id", 1, "name", "John Doe", "email", "john@example.com", "role", "admin"),
            Map.of("id", 2, "name", "Jane Smith", "email", "jane@example.com", "role", "user"),
            Map.of("id", 3, "name", "Bob Johnson", "email", "bob@example.com", "role", "user")
        ));
        
        database.put("products", List.of(
            Map.of("id", 1, "name", "Laptop", "price", 999.99, "stock", 10),
            Map.of("id", 2, "name", "Mouse", "price", 29.99, "stock", 50),
            Map.of("id", 3, "name", "Keyboard", "price", 79.99, "stock", 30)
        ));
        
        database.put("orders", List.of(
            Map.of("id", 1, "userId", 1, "productId", 1, "quantity", 1, "status", "completed"),
            Map.of("id", 2, "userId", 2, "productId", 2, "quantity", 2, "status", "processing"),
            Map.of("id", 3, "userId", 3, "productId", 3, "quantity", 1, "status", "shipped")
        ));
    }

    @Override
    public Response apply(Request request) {
        String table = request.table().toLowerCase();
        
        if (!database.containsKey(table)) {
            return new Response(false, null, "Table not found: " + table);
        }
        
        List<Map<String, Object>> data = new ArrayList<>(database.get(table));
        
        // Apply filters if provided
        if (request.filters() != null && !request.filters().isEmpty()) {
            data = data.stream()
                .filter(row -> matchesFilters(row, request.filters()))
                .toList();
        }
        
        // Apply limit if provided
        if (request.limit() != null && request.limit() > 0) {
            data = data.stream().limit(request.limit()).toList();
        }
        
        return new Response(true, data, "Query executed successfully");
    }
    
    private boolean matchesFilters(Map<String, Object> row, Map<String, Object> filters) {
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object rowValue = row.get(filter.getKey());
            Object filterValue = filter.getValue();
            
            if (rowValue == null || !rowValue.toString().equals(filterValue.toString())) {
                return false;
            }
        }
        return true;
    }

    public record Request(
        @JsonProperty("table") String table,
        @JsonProperty("filters") Map<String, Object> filters,
        @JsonProperty("limit") Integer limit
    ) {}

    public record Response(
        @JsonProperty("success") boolean success,
        @JsonProperty("data") List<Map<String, Object>> data,
        @JsonProperty("message") String message
    ) {}
}
