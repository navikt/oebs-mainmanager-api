FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:01d250226d847a5a0b05a8197285f8c310017d4582bf6ac42f21d5ff861833f6
ENV TZ="Europe/Oslo"
COPY target/oebs-mainmanager-api-*.jar app.jar
CMD ["-jar","app.jar"]