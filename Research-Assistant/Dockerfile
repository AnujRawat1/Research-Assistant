# Use an official Maven image to build the application
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code into the container & build the application
COPY src ./src
RUN  mvn clean package -DskipTests

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/Research-Assistant-0.0.1-SNAPSHOT.jar .

# Expose the application port
EXPOSE 8080

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "/app/Research-Assistant-0.0.1-SNAPSHOT.jar"]


