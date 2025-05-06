# Stage 1: Build the jar using Gradle
FROM gradle:8.6-jdk21 as build

WORKDIR /app
COPY . .

RUN ls -al /app
# Build jar file and clean cache
RUN ./gradlew clean && \
    ./gradlew bootJar --no-daemon && \
    rm -rf /home/gradle/.gradle/caches


RUN ls /app/build/libs
# Stage 2: Run the app
FROM eclipse-temurin:21-jdk-alpine AS runtime
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar


# Expose the default Spring Boot port
EXPOSE 8080

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]