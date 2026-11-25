# ==============================================================================
# Dockerfile for Census Management System (Java Spring Boot 3 + SQLite)
# Suitable for free cloud deployment on Render, Railway, Koyeb, Fly.io, etc.
# ==============================================================================

# Stage 1: Build application using Maven & Java 17
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy Maven POM and source tree
COPY pom.xml .
COPY src ./src

# Compile and package production JAR
RUN mvn clean package -DskipTests

# Stage 2: Lightweight JRE 17 Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy packaged JAR from build stage
COPY --from=builder /workspace/target/census-management-system-1.0.0.jar app.jar

# Cloud providers (Render, Railway, etc.) automatically set the PORT variable
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
