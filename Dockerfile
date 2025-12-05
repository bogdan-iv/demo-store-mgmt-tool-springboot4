# --- STAGE 1: Build the Application JAR ---
# Use a standard, stable Maven image with the latest OpenJDK
FROM maven:3.9.11-eclipse-temurin-25-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to cache dependencies efficiently
COPY pom.xml .

# Download dependencies (if pom.xml hasn't changed)
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the application (the standard clean package command)
RUN mvn clean package -DskipTests

# --- STAGE 2: Run the Application in a Minimal JRE ---
# Use a stable, minimal runtime image for OpenJDK 25
FROM eclipse-temurin:25-jre-alpine

# Set the working directory for the runtime stage
WORKDIR /app

# Copy the built JAR file from the 'build' stage into this new image
COPY --from=build /app/target/demo-store-mgmt-tool-springboot4-0.0.1-SNAPSHOT.jar app.jar

# Define the default command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the port where the Spring app runs (default is 8080)
EXPOSE 8080