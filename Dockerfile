# ==========================================
# Stage 1: Build the Application
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies first (optimizes Docker caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2