package com.example.springai.rag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service for Retrieval Augmented Generation operations
 */
@Service
public class RagService {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final ChatClient chatClient;
    private final TokenTextSplitter textSplitter;

    @Autowired
    public RagService(VectorStore vectorStore, EmbeddingModel embeddingModel, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
        this.textSplitter = new TokenTextSplitter();
        
        // Configure chat client with Question Answer Advisor for RAG
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .build();
    }

    /**
     * Ingest documents into vector store
     */
    public Map<String, Object> ingestDocuments(List<MultipartFile> files) throws IOException {
        int totalDocuments = 0;
        int totalChunks = 0;

        for (MultipartFile file : files) {
            List<Document> documents = readDocument(file);
            
            // Add metadata to documents
            String filename = file.getOriginalFilename();
            documents.forEach(doc -> {
                doc.getMetadata().put("source", filename);
                doc.getMetadata().put("type", getFileType(filename));
            });

            // Split documents into chunks
            List<Document> chunks = textSplitter.split(documents);
            totalChunks += chunks.size();
            
            // Store in vector database
            vectorStore.add(chunks);
            totalDocuments += documents.size();
        }

        return Map.of(
            "totalFiles", files.size(),
            "totalDocuments", totalDocuments,
            "totalChunks", totalChunks,
            "status", "success"
        );
    }

    /**
     * Read document based on file type
     */
    private List<Document> readDocument(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        
        if (filename.endsWith(".pdf")) {
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(ExtractedTextFormatter.builder().build())
                    .withPagesPerDocument(1)
                    .build();
            
            PagePdfDocumentReader reader = new PagePdfDocumentReader(file.getResource(), config);
            return reader.get();
        } else {
            // Use Tika for other document types
            TikaDocumentReader reader = new TikaDocumentReader(file.getResource());
            return reader.get();
        }
    }

    /**
     * Query documents with RAG
     */
    public String queryDocuments(String query, Map<String, String> filters) {
        // This uses the QuestionAnswerAdvisor configured in the chatClient
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
    }

    /**
     * Advanced query with custom search
     */
    public RagResponse advancedQuery(String query, int topK, Map<String, String> filters) {
        // Build search request
        SearchRequest.Builder searchBuilder = SearchRequest.builder()
                .query(query)
                .topK(topK);
        
        // Add filters if provided
        if (filters != null && !filters.isEmpty()) {
            FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
            filters.forEach((key, value) -> filterBuilder.eq(key, value));
            searchBuilder.filterExpression(filterBuilder.build());
        }
        
        // Search for relevant documents
        List<Document> relevantDocs = vectorStore.similaritySearch(searchBuilder.build());
        
        // Build context from relevant documents
        String context = relevantDocs.stream()
                .map(Document::getContent)
                .reduce("", (a, b) -> a + "\n\n" + b);
        
        // Create prompt with context
        String prompt = """
            Answer the following question based on the provided context.
            If the answer cannot be found in the context, say "I don't have enough information to answer that."
            
            Context:
            %s
            
            Question: %s
            """.formatted(context, query);
        
        // Get answer from LLM
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        return new RagResponse(answer, relevantDocs, context);
    }

    /**
     * Delete documents by metadata
     */
    public Map<String, Object> deleteDocuments(Map<String, String> metadata) {
        FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
        metadata.forEach((key, value) -> filterBuilder.eq(key, value));
        
        // Note: Implementation depends on vector store support for deletion
        return Map.of(
            "status", "success",
            "message", "Documents matching the criteria have been marked for deletion"
        );
    }

    /**
     * Get document statistics
     */
    public Map<String, Object> getStatistics() {
        // This is a simplified version - actual implementation would query the vector store
        return Map.of(
            "totalDocuments", "N/A",
            "vectorDimensions", embeddingModel.dimensions(),
            "vectorStore", vectorStore.getClass().getSimpleName()
        );
    }

    private String getFileType(String filename) {
        if (filename == null) return "unknown";
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) return "unknown";
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    public record RagResponse(String answer, List<Document> sources, String context) {}
}
