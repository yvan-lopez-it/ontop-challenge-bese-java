FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B package

FROM openjdk:17-alpine
COPY --from=builder /app/target/ontop-challenge-bese-java-0.0.1-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar"]