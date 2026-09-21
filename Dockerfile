# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application using Java 17
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]