# Multi-stage Dockerfile for Spring Boot Todo Backend
ARG JAVA_VERSION=26

# ==========================================
# 1. Build Stage
# ==========================================
FROM eclipse-temurin:${JAVA_VERSION}-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and POM configuration first to leverage Docker layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Ensure wrapper script has execution permissions
RUN chmod +x mvnw

# Download dependencies in offline mode (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B || true

# Copy project source files
COPY src src

# Package the application (skip tests for faster container builds)
RUN ./mvnw clean package -DskipTests

# ==========================================
# 2. Runtime Stage
# ==========================================
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine

# Create a non-root group and user for security compliance
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the generated JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership to the spring user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose Spring Boot default port
EXPOSE 8080

# Configure JVM flags suitable for container environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Use exec to ensure signals (SIGTERM/SIGINT) are passed to the JVM for graceful shutdown
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
