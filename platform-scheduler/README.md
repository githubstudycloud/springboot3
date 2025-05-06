# Platform Scheduler Module

## Overview

The Platform Scheduler module provides a robust, distributed task scheduling system based on Domain-Driven Design (DDD) and Hexagonal Architecture principles. It supports various task types, complex dependency relationships, and efficient cluster coordination.

## Key Features

- **Flexible Job Scheduling**: Support for cron-based schedules, one-time executions, and event-triggered jobs
- **Dependency Management**: Define complex job dependencies with different dependency types
- **Distributed Execution**: Coordinate task execution across multiple nodes with load balancing
- **Fault Tolerance**: Retry mechanisms, timeout control, and failure recovery
- **Monitoring & Tracking**: Comprehensive metrics, logs, and execution history
- **Pluggable Architecture**: Extend with custom task types and executors
- **REST API**: Full-featured API for integration with other systems

## Architecture

The Platform Scheduler follows a clean, hexagonal architecture with well-defined boundaries:

```
┌─────────────────────────────────────────────────────────────────┐
│                      Interfaces Layer (REST API)                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                      Application Layer                           │
│                                                                  │
│  ┌─────────────────┐    ┌─────────────────┐   ┌──────────────┐  │
│  │ Job Management  │    │ Task Execution  │   │    Cluster   │  │
│  │    Service      │    │    Service      │   │  Management  │  │
│  └─────────────────┘    └─────────────────┘   └──────────────┘  │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                        Domain Layer                              │
│                                                                  │
│  ┌─────────┐   ┌───────────────┐   ┌──────────┐   ┌───────────┐ │
│  │   Job   │   │TaskInstance   │   │ Executor │   │  Cluster  │ │
│  │         │   │               │   │          │   │           │ │
│  └─────────┘   └───────────────┘   └──────────┘   └───────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                   Infrastructure Layer                           │
│                                                                  │
│  ┌──────────────┐   ┌────────────────┐   ┌─────────────────────┐│
│  │  Repository  │   │Distributed Lock│   │  Cluster Coordinator ││
│  │ Implementations│  │                │   │                     ││
│  └──────────────┘   └────────────────┘   └─────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### Domain Layer

The core of the system contains the business logic and domain models:

- **Job**: Represents a scheduled job with its properties, schedule, and dependencies
- **TaskInstance**: An actual execution instance of a job at a specific time
- **Executor**: A node in the cluster that can execute tasks
- **Domain Events**: Events triggered during the lifecycle of jobs and tasks

### Application Layer

Orchestrates the domain objects to fulfill use cases:

- **JobManagementService**: Creates, updates, and manages job definitions
- **TaskExecutionService**: Handles the execution and lifecycle of tasks
- **ClusterManagementService**: Manages executor nodes and cluster coordination

### Infrastructure Layer

Provides implementations for persistence and external services:

- **Repositories**: Persistent storage for domain objects
- **Distributed Lock**: Ensures exclusive access to shared resources
- **Cluster Coordinator**: Coordinates task execution across nodes

### Interfaces Layer

Exposes the functionality to external systems:

- **REST Controllers**: API endpoints for job and task management
- **Request/Response DTOs**: Data transfer objects for the API

## Setup and Configuration

### Dependencies

The Platform Scheduler module requires:

- Spring Boot 3.x
- Redis for distributed locking and coordination
- MySQL for persistent storage
- Java 21

### Database Configuration

Database tables are created using Flyway migrations in the `resources/db/migration` directory.

### Properties Configuration

Configure the scheduler properties in `application.properties` or `application.yml`:

```yaml
platform:
  scheduler:
    # Default retry settings
    retry:
      max-attempts: 3
      delay-seconds: 60
      strategy: EXPONENTIAL_BACKOFF
    
    # Executor configuration
    executor:
      heartbeat-interval-seconds: 30
      timeout-seconds: 120
      
    # Job settings
    job:
      max-concurrent-jobs: 100
```

## API Reference

### Job Management API

- `POST /api/v1/jobs` - Create a new job
- `GET /api/v1/jobs` - List all jobs
- `GET /api/v1/jobs/active` - List active jobs
- `GET /api/v1/jobs/{jobId}` - Get job details
- `PUT /api/v1/jobs/{jobId}` - Update job
- `POST /api/v1/jobs/{jobId}/activate` - Activate job
- `POST /api/v1/jobs/{jobId}/pause` - Pause job
- `POST /api/v1/jobs/{jobId}/resume` - Resume job
- `POST /api/v1/jobs/{jobId}/disable` - Disable job
- `DELETE /api/v1/jobs/{jobId}` - Delete job

### Task Execution API

- `POST /api/v1/tasks` - Schedule a task
- `GET /api/v1/tasks` - List all tasks
- `GET /api/v1/tasks/job/{jobId}` - List tasks by job
- `GET /api/v1/tasks/status/{status}` - List tasks by status
- `GET /api/v1/tasks/{taskId}` - Get task details
- `POST /api/v1/tasks/{taskId}/assign` - Assign task to executor
- `POST /api/v1/tasks/{taskId}/start` - Start task execution
- `POST /api/v1/tasks/{taskId}/complete` - Complete task
- `POST /api/v1/tasks/{taskId}/fail` - Fail task
- `POST /api/v1/tasks/{taskId}/cancel` - Cancel task
- `POST /api/v1/tasks/{taskId}/log` - Add log entry
- `GET /api/v1/tasks/{taskId}/logs` - Get task logs

### Cluster Management API

- `POST /api/v1/cluster/executors` - Register executor
- `GET /api/v1/cluster/executors` - List all executors
- `GET /api/v1/cluster/executors/active` - List active executors
- `GET /api/v1/cluster/executors/{executorId}` - Get executor details
- `POST /api/v1/cluster/executors/{executorId}/activate` - Activate executor
- `POST /api/v1/cluster/executors/{executorId}/deactivate` - Deactivate executor
- `POST /api/v1/cluster/executors/{executorId}/heartbeat` - Update heartbeat
- `GET /api/v1/cluster/tasks/{taskId}/executor` - Get task's executor
- `POST /api/v1/cluster/maintenance/detect-failed-executors` - Detect failed executors

## Extending the Scheduler

### Custom Task Types

To implement a custom task type:

1. Define a new `JobType` enum value
2. Create a task executor implementation
3. Register the executor with the system

### Custom Triggers

To implement a custom trigger type:

1. Extend the `JobTrigger` class
2. Implement the trigger logic in the `getNextFireTime` method
3. Register the trigger type with the system

## Integration with Other Modules

### Report Engine Integration

The scheduler integrates with the report engine to schedule report generation tasks:

1. Define a report generation job with parameters
2. The report engine receives the task execution request
3. Results are stored back in the scheduler for tracking

### Data Visualization Integration

For data visualization integration:

1. Use the scheduler to prepare data for visualization
2. Schedule periodic data updates
3. Send notifications when new visualizations are ready

## Deployment

### Single Instance

For single-instance deployment:

1. Configure database and Redis connection
2. Set `platform.scheduler.cluster.enabled=false`
3. Deploy the application

### Cluster Deployment

For cluster deployment:

1. Configure database and Redis connection
2. Set `platform.scheduler.cluster.enabled=true`
3. Configure node ID with `platform.scheduler.cluster.node-id=node-1`
4. Deploy multiple instances with different node IDs

## High Availability

To ensure high availability of the scheduler:

1. Deploy multiple instances behind a load balancer
2. Configure Redis sentinel or cluster for distributed locking
3. Use a replicated database setup
4. Implement regular health checks and auto-recovery

## Monitoring and Operations

### Health Checks

The scheduler provides health endpoints:

- `/actuator/health` - Overall system health
- `/actuator/health/liveness` - Check if the application is live
- `/actuator/health/readiness` - Check if the application is ready to serve requests

### Metrics

Key metrics exposed via Prometheus at `/actuator/prometheus`:

- `scheduler_jobs_active_count` - Number of active jobs
- `scheduler_tasks_scheduled_count` - Number of scheduled tasks
- `scheduler_tasks_completed_count` - Number of completed tasks
- `scheduler_tasks_failed_count` - Number of failed tasks
- `scheduler_executor_count` - Number of active executors

### Logging

Logging configuration can be customized in `application.yml`:

```yaml
logging:
  level:
    com.example.platform.scheduler: INFO
    # Set to DEBUG for more detailed logs
    # com.example.platform.scheduler.domain: DEBUG
```

## Security

### Authentication and Authorization

The scheduler API supports the following security options:

1. OAuth2/JWT-based authentication
2. Role-based access control for API endpoints
3. API key authentication for executor nodes

### Configuration

Security settings in `application.yml`:

```yaml
platform:
  scheduler:
    security:
      enabled: true
      executor-auth-token-header: X-Executor-Auth-Token
```

## Future Enhancements

Planned features for future releases:

1. GraphQL API support
2. WebSocket notifications for real-time task updates
3. Enhanced task dependency graph visualization
4. Dynamic resource allocation based on task priority
5. Integration with Kubernetes for dynamic scaling

## Contributing

### Development Workflow

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

### Coding Standards

Follow the project's coding standards:

- Use comprehensive Javadoc comments
- Maintain test coverage above 80%
- Follow the hexagonal architecture principles
- Use domain language consistently

## License

This module is part of the platform project and is licensed under the [MIT License](LICENSE).

## Acknowledgments

- Quartz for inspiration on job scheduling concepts
- Spring Framework for the foundation
- DDD community for architectural guidance
