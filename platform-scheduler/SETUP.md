# Platform Scheduler Setup Guide

This document provides step-by-step instructions for setting up and configuring the Platform Scheduler module.

## Prerequisites

Before you begin, ensure you have the following:

- Java 21 or higher
- Maven 3.8+ or Gradle 7.5+
- MySQL 5.7+ or compatible database
- Redis 7.x+
- Git

## Setup Steps

### 1. Clone Repository

First, clone the platform parent repository:

```bash
git clone https://github.com/yourusername/platform.git
cd platform
```

### 2. Configure Database

Create a MySQL database for the scheduler:

```sql
CREATE DATABASE platform_scheduler CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'scheduler_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON platform_scheduler.* TO 'scheduler_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Redis

Ensure Redis is running and accessible:

```bash
# Test Redis connection
redis-cli ping
```

Expected response: `PONG`

### 4. Configure Application Properties

Create or modify `platform-scheduler/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/platform_scheduler?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: scheduler_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    show-sql: false
  redis:
    host: localhost
    port: 6379
    # password: your_redis_password  # Uncomment if Redis requires authentication

platform:
  scheduler:
    executor:
      heartbeat-interval-seconds: 30
      timeout-seconds: 120
    cluster:
      enabled: true
      node-id: ${SCHEDULER_NODE_ID:node-1}
    quartz:
      auto-startup: true
      startup-delay: 10
      overwrite-existing-jobs: true
      wait-for-jobs-to-complete-on-shutdown: true
    retry:
      max-attempts: 3
      delay-seconds: 60
      strategy: EXPONENTIAL_BACKOFF

logging:
  level:
    com.example.platform.scheduler: INFO
    org.springframework: INFO
    org.hibernate: INFO
```

### 5. Configure Application Bootstrap

Create or modify `platform-scheduler/src/main/resources/bootstrap.yml`:

```yaml
spring:
  application:
    name: platform-scheduler
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
server:
  port: ${SERVER_PORT:8081}
```

### 6. Build the Module

Build the scheduler module using Maven:

```bash
# From the parent project directory
mvn clean install -pl platform-scheduler -am
```

Or using Gradle:

```bash
# From the parent project directory
gradle :platform-scheduler:clean :platform-scheduler:build
```

### 7. Run the Application

Run the application:

```bash
# Using Maven
mvn spring-boot:run -pl platform-scheduler

# Using Gradle
gradle :platform-scheduler:bootRun

# Using Java
java -jar platform-scheduler/target/platform-scheduler-1.0.0.jar
```

### 8. Verify Installation

Check if the application is running by accessing the health endpoint:

```bash
curl http://localhost:8081/actuator/health
```

Expected response:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "redis": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

## Cluster Setup

For running the scheduler in a cluster:

1. Set up multiple instances with different node IDs:

```bash
# Node 1
java -jar platform-scheduler.jar --platform.scheduler.cluster.node-id=node-1 --server.port=8081

# Node 2
java -jar platform-scheduler.jar --platform.scheduler.cluster.node-id=node-2 --server.port=8082

# Node 3
java -jar platform-scheduler.jar --platform.scheduler.cluster.node-id=node-3 --server.port=8083
```

2. Configure a load balancer to distribute requests across nodes

3. Ensure all nodes use the same Redis instance for coordination

## Integration with Other Modules

### Connecting to platform-data-visualization

To integrate with the data visualization module:

1. Add the data visualization dependency to the scheduler:

```xml
<dependency>
    <groupId>com.example.platform</groupId>
    <artifactId>platform-data-visualization</artifactId>
    <version>${project.version}</version>
</dependency>
```

2. Configure the integration in `application.yml`:

```yaml
platform:
  visualization:
    endpoint: http://localhost:8082/api/v1/visualizations
    refresh-interval-seconds: 300
```

### Connecting to platform-report-engine

To integrate with the report engine:

1. Add the report engine dependency:

```xml
<dependency>
    <groupId>com.example.platform</groupId>
    <artifactId>platform-report-engine</artifactId>
    <version>${project.version}</version>
</dependency>
```

2. Configure the integration in `application.yml`:

```yaml
platform:
  report-engine:
    endpoint: http://localhost:8083/api/v1/reports
    max-concurrent-reports: 10
```

## Troubleshooting

### Common Issues

#### Database Connection Issues

If you face database connection issues:

```
Error creating bean with name 'entityManagerFactory': Cannot resolve reference to bean 'dataSource'
```

Check your database configuration and ensure MySQL is running:

```bash
mysql -u scheduler_user -p -e "SELECT 1"
```

#### Redis Connection Issues

If Redis connection fails:

```
RedisConnectionFailureException: Unable to connect to Redis
```

Verify Redis status:

```bash
redis-cli ping
```

#### Task Execution Issues

If tasks fail to execute:

1. Check executor logs
2. Ensure cluster coordination is working properly
3. Verify that job dependencies are correctly configured

## Security Configuration

### Enabling Basic Authentication

To enable basic authentication:

1. Add Spring Security dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2. Configure security in `application.yml`:

```yaml
spring:
  security:
    user:
      name: admin
      password: your_secure_password
```

### Enabling OAuth2/JWT

For OAuth2/JWT configuration:

1. Add the OAuth2 dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

2. Configure in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-auth-server.com/issuer
          jwk-set-uri: https://your-auth-server.com/.well-known/jwks.json
```

## Maintenance

### Backup and Restore

To backup the scheduler database:

```bash
mysqldump -u scheduler_user -p platform_scheduler > scheduler_backup.sql
```

To restore:

```bash
mysql -u scheduler_user -p platform_scheduler < scheduler_backup.sql
```

### Monitoring

Enable Prometheus metrics:

1. Add the dependency:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

2. Access metrics at:

```
http://localhost:8081/actuator/prometheus
```

## Conclusion

You have successfully set up the Platform Scheduler module. For advanced configuration and usage, refer to the [README.md](README.md) and API documentation.
