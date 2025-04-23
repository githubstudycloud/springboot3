FROM openjdk:11-jre-slim

LABEL maintainer="platform-team@example.com"

# Add app user
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

# Configure working directory
WORKDIR /app

# Set app user as owner
RUN chown -R appuser:appgroup /app

# Set up environment variables
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE="docker"

# Copy application JAR file
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Expose default application port
EXPOSE 8080

# Use non-root user to run application
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -q -O /dev/null http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
