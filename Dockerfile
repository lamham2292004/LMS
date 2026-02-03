# ==============================================================================
# Multi-stage build for Spring Boot LMS Application
# Optimized for production with security best practices
# ==============================================================================

# ==============================================================================
# Stage 1: Build the application
# ==============================================================================
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Build arguments for flexibility
ARG MAVEN_OPTS="-Xmx1024m"
ARG SKIP_TESTS=true

WORKDIR /app

# Copy Maven wrapper and config files first (better layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B -q -Djava.net.preferIPv4Stack=true

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests=${SKIP_TESTS} -B -q -Djava.net.preferIPv4Stack=true

# ==============================================================================
# Stage 2: Create the runtime image
# ==============================================================================
FROM eclipse-temurin:17-jre-alpine AS runtime

# Labels for metadata
LABEL maintainer="LMS Team"
LABEL version="1.0.0"
LABEL description="LMS - Learning Management System Spring Boot Application"

WORKDIR /app

# Install necessary packages for healthcheck, timezones, and debugging
# "Install all environments" - ensuring all system deps are present
RUN apk add --no-cache \
    curl \
    tzdata \
    netcat-openbsd \
    fontconfig \
    ttf-dejavu \
    && rm -rf /var/cache/apk/*

# Set timezone
ENV TZ=Asia/Ho_Chi_Minh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create directories including uploads and logs
RUN mkdir -p /app/uploads /app/logs /app/config && \
    chmod 777 /app/uploads /app/logs

# Copy entrypoint script
COPY docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Copy the built JAR from build stage
# Explicitly copy the main application jar
COPY --from=build /app/target/LMS-*.jar /app/app.jar

# Create a non-root user for security
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring && \
    chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose the application port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8083/actuator/health || exit 1

# JVM options optimized for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -Djava.security.egd=file:/dev/./urandom"

# Spring profiles (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod

# Use entrypoint script
ENTRYPOINT ["/app/docker-entrypoint.sh"]
