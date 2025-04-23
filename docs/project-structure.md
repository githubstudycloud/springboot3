# Platform Project Structure

## Overview

This document provides an overview of the project structure for the platform's microservices architecture. The platform is built using Spring Boot and Spring Cloud technologies to create a scalable, resilient, and maintainable system.

## Top-Level Project Structure

The project follows a multi-module Maven project structure:

```
platform-parent/
├── docs/                              # Project documentation
├── platform-common/                   # Shared utilities and components
├── platform-collect/                  # Data collection service
├── platform-fluxcore/                 # Core processing service
├── platform-buss-dashboard/           # Business dashboard
├── platform-monitor-dashboard/        # Monitoring dashboard
├── platform-gateway/                  # API gateway
├── platform-scheduler/                # Main scheduler service
├── platform-scheduler-register/       # Scheduler registration service
├── platform-scheduler-query/          # Scheduler query service
├── platform-config/                   # Configuration service
├── platform-registry/                 # Service registry (Eureka/Admin)
└── pom.xml                            # Parent POM file
```

## Key Components

### 1. platform-common

Provides shared utilities, models, and components used across all services.

```
platform-common/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── platform/
│                   └── common/
│                       ├── aspect/                # AOP aspects
│                       ├── config/                # Common configurations
│                       ├── constants/             # Shared constants
│                       ├── exception/             # Exception classes
│                       ├── model/                 # Common data models
│                       └── utils/                 # Utility classes
└── pom.xml
```

### 2. platform-gateway

API Gateway that routes client requests to the appropriate services.

```
platform-gateway/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── gateway/
│       │               ├── config/                # Gateway configuration
│       │               ├── controller/            # Fallback controllers
│       │               └── filter/                # Gateway filters
│       └── resources/
│           └── application.yml                    # Gateway configuration
└── pom.xml
```

### 3. platform-registry

Service registry for service discovery and health monitoring.

```
platform-registry/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── registry/
│       │               └── config/                # Security configuration
│       └── resources/
│           └── application.yml                    # Registry configuration
└── pom.xml
```

### 4. platform-config

Centralized configuration management for all services.

```
platform-config/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── config/
│       └── resources/
│           ├── application.yml                    # Config server configuration
│           └── config/                            # Service configurations
└── pom.xml
```

### 5. platform-scheduler Services

Three separate services for task scheduling functionality.

```
platform-scheduler/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── scheduler/
│       │               ├── config/                # Scheduler configuration
│       │               ├── controller/            # REST controllers
│       │               ├── service/               # Business logic
│       │               ├── repository/            # Data access
│       │               └── model/                 # Data models
│       └── resources/
└── pom.xml

platform-scheduler-register/
└── [Similar structure to platform-scheduler]

platform-scheduler-query/
└── [Similar structure to platform-scheduler]
```

### 6. Business Services

Services for business functionality.

```
platform-collect/
└── [Standard microservice structure]

platform-fluxcore/
└── [Standard microservice structure]

platform-buss-dashboard/
└── [Standard microservice structure]

platform-monitor-dashboard/
└── [Standard microservice structure]
```

## Standard Microservice Structure

Each microservice follows a similar structure with the following organization:

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── platform/
│   │   │           └── [service]/
│   │   │               ├── config/                # Service configurations
│   │   │               ├── controller/            # REST controllers/endpoints
│   │   │               ├── service/               # Business logic
│   │   │               │   ├── impl/              # Service implementations
│   │   │               │   └── [interfaces]       # Service interfaces
│   │   │               ├── repository/            # Data access
│   │   │               ├── model/                 # Data models
│   │   │               │   ├── entity/            # JPA entities
│   │   │               │   └── dto/               # Data transfer objects
│   │   │               ├── mapper/                # Object mappers
│   │   │               ├── client/                # Feign clients
│   │   │               ├── listener/              # Message listeners
│   │   │               └── exception/             # Service-specific exceptions
│   │   └── resources/
│   │       ├── application.yml                    # Application configuration
│   │       ├── bootstrap.yml                      # Bootstrap configuration
│   │       └── [other resource files]
│   └── test/
│       └── java/
│           └── com/
│               └── platform/
│                   └── [service]/
│                       ├── controller/            # Controller tests
│                       ├── service/               # Service tests
│                       └── repository/            # Repository tests
└── pom.xml
```

## Maven Configuration

The project uses a parent POM with dependency management for consistent versions across all modules. Key dependencies include:

1. **Spring Boot**: Core framework for microservices
2. **Spring Cloud**: Microservices coordination
   - Netflix Eureka: Service discovery
   - Spring Cloud Gateway: API gateway
   - Spring Cloud Config: Centralized configuration
   - Spring Cloud OpenFeign: Declarative REST clients
   - Spring Cloud Circuit Breaker: Resilience patterns
3. **Database Access**:
   - Spring Data JPA: Database access
   - MySQL Connector: Database driver
   - Redis: Caching and distributed data structures
4. **Messaging**:
   - Spring AMQP: RabbitMQ integration
   - Spring Kafka: Kafka integration
5. **Monitoring and Management**:
   - Spring Boot Actuator: Monitoring and metrics
   - Micrometer: Metrics collection
   - Prometheus Integration: Metrics storage
   - Spring Boot Admin: Administration UI
6. **Security**:
   - Spring Security: Authentication and authorization
   - JWT: Token-based authentication
7. **Development Utilities**:
   - Lombok: Boilerplate reduction
   - Apache Commons: Utility libraries
   - Jackson: JSON processing
   - Swagger: API documentation

## Module Communication

Services communicate with each other through:

1. **REST APIs**: Synchronous communication via Spring Cloud OpenFeign
2. **Message Queues**: Asynchronous communication via RabbitMQ or Kafka
3. **Shared Database**: When necessary, with careful schema design
4. **Event Streaming**: For event-driven architecture patterns

## Deployment Structure

The platform is designed for containerized deployment with Docker and Kubernetes:

```
kubernetes/
├── base/                              # Base configurations
│   ├── services/                      # Service deployments
│   ├── ingress/                       # Ingress rules
│   ├── configmaps/                    # ConfigMaps
│   └── secrets/                       # Secrets
├── environments/                      # Environment-specific configurations
│   ├── dev/
│   ├── test/
│   └── prod/
└── monitoring/                        # Monitoring configurations
    ├── prometheus/
    ├── grafana/
    └── alerts/
```

## Development Workflow

The platform supports the following development workflows:

1. **Local Development**:
   - Each service can be run locally with Spring Boot
   - Shared infrastructure (databases, message queues) can be run in Docker
   - Spring profiles configure environment-specific settings

2. **CI/CD Pipeline**:
   - Automated testing: Unit, integration, and end-to-end tests
   - Static code analysis
   - Docker image building
   - Kubernetes deployment
   - Canary releases and rollbacks

3. **Monitoring and Operations**:
   - Centralized logging
   - Distributed tracing
   - Metrics and dashboards
   - Alerting

## Conclusion

This project structure provides a solid foundation for building a scalable, maintainable, and resilient platform. The modular design allows teams to work independently on different services while ensuring consistency across the platform.
