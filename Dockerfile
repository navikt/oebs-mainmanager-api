FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:c89bea04511a0c78d2fe0e4f96cf99a2236bf27beda1ed17a5272a790c65f82c
ENV TZ="Europe/Oslo"
COPY target/oebs-mainmanager-api-*.jar app.jar
CMD ["-jar","app.jar"]