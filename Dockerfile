# FROM gcr.io/distroless/java21
# FROM eclipse-temurin:21-jdk

# Stage 1: Build your Java application
FROM eclipse-temurin:21-jdk AS build

# Install network utilities for debugging during the build process
RUN apt-get update && apt-get install -y iproute2 iputils-ping telnet

FROM eclipse-temurin:21-jre-bookworm AS runtime

WORKDIR /app

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV TZ="Europe/Oslo"
ENTRYPOINT ["java","-jar","app.jar"]

