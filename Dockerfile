# Stage 1: Build your Java application
# Use the correct tag for the JDK image
FROM eclipse-temurin:21-jdk AS build

# Use curl, bash, etc., during the build process
# ...

# Set up your build environment
WORKDIR /app
COPY . .
RUN ./mvn package # Or mvn package, etc.

# Stage 2: Create the final, lean production image
# Use the minimal JRE-only image with the correct tag
FROM eclipse-temurin:21-jre AS runtime

# Install network utilities for testing and diagnostics
RUN apt-get update && \
 apt-get install -y --no-install-recommends \
 iputils-ping \
 iproute2 \
 telnet \
 && rm -rf /var/lib/apt/lists/* \
 \
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV TZ="Europe/Oslo"
ENTRYPOINT ["java","-jar","app.jar"]