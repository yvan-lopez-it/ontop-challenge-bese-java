# Use a multi-stage build

# Development stage
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B clean package

# Production stage
FROM openjdk:17-alpine
COPY --from=builder /app/target/ontop-challenge-bese-java-0.0.1-SNAPSHOT.jar /app.jar
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
CMD ["java", "-jar", "/app.jar"]
