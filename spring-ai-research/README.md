# Spring AI Research Module

This module demonstrates the latest features of Spring AI 1.0 GA.

## Features Demonstrated

1. **ChatClient** - Core API for AI interactions
2. **Structured Output** - Mapping AI responses to POJOs
3. **Tool Calling** - Enabling AI to call functions
4. **RAG (Retrieval Augmented Generation)** - Context-aware AI responses
5. **Vector Database Integration** - Using PGVector for embeddings
6. **Multi-modal Support** - Text, image, and audio processing
7. **Multiple AI Providers** - OpenAI, Anthropic, Ollama integration

## Project Structure

```
spring-ai-research/
├── src/main/java/com/example/springai/research/
│   ├── SpringAiResearchApplication.java    # Main application class
│   ├── config/                            # Configuration classes
│   ├── controller/                        # REST controllers
│   ├── service/                          # Service layer
│   ├── model/                            # Domain models
│   └── tools/                            # Tool/Function definitions
├── src/main/resources/
│   ├── application.yml                    # Application configuration
│   └── static/                           # Static resources
└── pom.xml                               # Maven configuration
```

## Getting Started

1. **Prerequisites**
   - Java 17+
   - Maven 3.8+
   - Docker (for PGVector)
   - API Keys for AI providers (optional)

2. **Environment Variables**
   ```bash
   export OPENAI_API_KEY=your-openai-key
   export ANTHROPIC_API_KEY=your-anthropic-key
   ```

3. **Running the Application**
   ```bash
   mvn spring-boot:run
   ```

## Next Steps

The implementation will be added incrementally to avoid output length issues.
Each feature will be implemented in its own package with proper documentation.
