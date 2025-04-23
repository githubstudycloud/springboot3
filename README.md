# Platform Microservices Architecture

## Overview

This project implements a comprehensive microservices-based platform for distributed data collection, processing, monitoring, and management. The architecture is designed to be scalable, resilient, and maintainable, with a focus on performance and reliability.

## Architecture

The platform is built on a microservices architecture with the following key components:

### Core Services

- **platform-collect**: Data ingestion and collection service
- **platform-fluxcore**: Core data processing engine
- **platform-buss-dashboard**: Business monitoring and visualization dashboard
- **platform-monitor-dashboard**: System health and performance monitoring dashboard
- **platform-gateway**: API gateway for routing and cross-cutting concerns
- **platform-scheduler**: Task scheduling and execution service
- **platform-scheduler-register**: Task registration and management service
- **platform-scheduler-query**: Task status and history query service

### Infrastructure Services

- **platform-config**: Centralized configuration management
- **platform-registry**: Service discovery and registration
- **platform-common**: Shared utilities and components

### Supporting Systems

- Logging and audit system
- Message queue
- Distributed transaction management
- Authentication and authorization center
- Alerting system
- Data storage and caching
- CI/CD containerization
- Module communication
- Gateway canary deployment and rollback
- Documentation and API management

## Technology Stack

- **Java 11**: Core programming language
- **Spring Boot**: Microservices framework
- **Spring Cloud**: Microservices coordination
  - Netflix Eureka: Service discovery
  - Spring Cloud Gateway: API gateway
  - Spring Cloud Config: Centralized configuration
  - Spring Cloud OpenFeign: Declarative REST clients
  - Spring Cloud Circuit Breaker: Resilience patterns
- **Databases**:
  - MySQL: Relational data storage
  - Redis: Caching and rate limiting
  - Elasticsearch: Search and analytics
- **Messaging**:
  - RabbitMQ: Message broker for asynchronous communication
- **Monitoring**:
  - Prometheus: Metrics collection and storage
  - Grafana: Metrics visualization
  - Zipkin: Distributed tracing
  - Spring Boot Admin: Application monitoring and management
- **Deployment**:
  - Docker: Containerization
  - Kubernetes: Container orchestration

## Project Structure

The project follows a multi-module Maven project structure. See [Project Structure](docs/project-structure.md) for detailed information.

## Getting Started

### Prerequisites

- JDK 11 or higher
- Maven 3.6.0 or higher
- Docker and Docker Compose
- IDE (IntelliJ IDEA recommended)

### Building the Project

```bash
# Clone the repository
git clone https://github.com/yourusername/platform-parent.git
cd platform-parent

# Build the project
mvn clean install
```

### Running Locally with Docker Compose

```bash
# Start all services
cd docker
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f [service-name]

# Stop all services
docker-compose down
```

### Service Access

- **Eureka Dashboard**: http://localhost:8761
- **Spring Boot Admin**: http://localhost:8761/admin
- **API Gateway**: http://localhost:8080
- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9090
- **Zipkin**: http://localhost:9411
- **RabbitMQ Management**: http://localhost:15672

## Development Guidelines

### Code Style

The project follows the standard Java code style with the following additional guidelines:

- Use lombok to reduce boilerplate code
- Follow the microservice design patterns
- Use appropriate exception handling with custom exceptions
- Document public APIs with Javadoc
- Write comprehensive unit and integration tests

### Branching Strategy

- `main`: Production-ready code
- `develop`: Integration branch for development
- `feature/*`: Feature development branches
- `release/*`: Release preparation branches
- `hotfix/*`: Hot fixes for production issues

### CI/CD Pipeline

The project uses a CI/CD pipeline for automated testing, building, and deployment:

1. **Build & Test**: Compile code and run tests
2. **Quality Analysis**: Static code analysis
3. **Docker Build**: Build Docker images
4. **Deploy to Test**: Deploy to test environment
5. **Integration Tests**: Run integration tests
6. **Deploy to Staging**: Deploy to staging environment
7. **User Acceptance Tests**: Run UAT
8. **Deploy to Production**: Deploy to production environment

## Monitoring and Operations

### Health Checks

Each service exposes health check endpoints via Spring Boot Actuator:

```
GET /actuator/health
```

### Metrics

Metrics are collected by Prometheus and visualized in Grafana dashboards:

- JVM metrics (memory, CPU, GC)
- HTTP request metrics (count, latency, errors)
- Business metrics (specific to each service)

### Logging

Logs are centralized and can be accessed through the Kibana dashboard or directly from the Docker logs.

## Key Features

- **Scalability**: Horizontal scaling of services
- **Resilience**: Circuit breakers, retries, and timeouts
- **Observability**: Comprehensive logging, metrics, and tracing
- **Security**: Authentication, authorization, and secure communication
- **Performance**: Caching, asynchronous processing, and optimized data access
- **Maintainability**: Modular design and clear separation of concerns

## Documentation

- [Architecture Overview](docs/architecture-overview.md)
- [Project Structure](docs/project-structure.md)
- [Scheduler Design](docs/scheduler-design.md)
- [API Documentation](docs/api-documentation.md)
- [Deployment Guide](docs/deployment-guide.md)
- [Monitoring Guide](docs/monitoring-guide.md)

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
