# Build Stage
FROM maven:3.9.9-amazoncorretto-23-alpine AS build
WORKDIR /app
COPY pomdocker.xml pom.xml
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]