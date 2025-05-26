package com.example.springai.rag.controller;

import com.example.springai.rag.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for RAG operations
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    @Autowired
    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * Upload and ingest documents
     */
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingestDocuments(
            @RequestParam("files") List<MultipartFile> files) {
        try {
            Map<String, Object> result = ragService.ingestDocuments(files);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to ingest documents: " + e.getMessage()));
        }
    }

    /**
     * Query documents using RAG
     */
    @PostMapping("/query")
    public ResponseEntity<String> queryDocuments(
            @RequestBody QueryRequest request) {
        String answer = ragService.queryDocuments(request.query(), request.filters());
        return ResponseEntity.ok(answer);
    }

    /**
     * Advanced query with sources
     */
    @PostMapping("/query/advanced")
    public ResponseEntity<RagService.RagResponse> advancedQuery(
            @RequestBody AdvancedQueryRequest request) {
        RagService.RagResponse response = ragService.advancedQuery(
                request.query(), 
                request.topK() != null ? request.topK() : 5,
                request.filters()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Delete documents by metadata
     */
    @DeleteMapping("/documents")
    public ResponseEntity<Map<String, Object>> deleteDocuments(
            @RequestBody Map<String, String> metadata) {
        Map<String, Object> result = ragService.deleteDocuments(metadata);
        return ResponseEntity.ok(result);
    }

    /**
     * Get RAG statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = ragService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Chat with documents - combines RAG with conversational memory
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithDocuments(
            @RequestBody ChatRequest request) {
        // This would maintain conversation history
        String answer = ragService.queryDocuments(request.message(), null);
        return ResponseEntity.ok(new ChatResponse(answer, request.conversationId()));
    }

    // Request/Response DTOs
    public record QueryRequest(String query, Map<String, String> filters) {}
    public record AdvancedQueryRequest(String query, Integer topK, Map<String, String> filters) {}
    public record ChatRequest(String message, String conversationId) {}
    public record ChatResponse(String response, String conversationId) {}
}
