# Stage 1: Build a temporary image to get bash and curl
FROM debian:stable-slim as builder

# Install bash and curl in the builder stage
RUN apt-get update && apt-get install -y \
    bash \
    curl \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Stage 2: Build the final distroless image
FROM gcr.io/distroless/java21

# Copy bash and curl from the builder stage
COPY --from=builder /bin/bash /bin/bash
COPY --from=builder /usr/bin/curl /usr/bin/curl
COPY --from=builder /lib/x86_64-linux-gnu/libtinfo.so.6 /lib/x86_64-linux-gnu/
COPY --from=builder /lib/x86_64-linux-gnu/libncursesw.so.6 /lib/x86_64-linux-gnu/
COPY --from=builder /lib/x86_64-linux-gnu/libcurl.so.4 /lib/x86_64-linux-gnu/

# FROM gcr.io/distroless/java21

WORKDIR /app

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV TZ="Europe/Oslo"
ENTRYPOINT ["java","-jar","app.jar"]

