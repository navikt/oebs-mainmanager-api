FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:5b89b27479236d36d96096e4666874574699b9bbb3edc2ea6029a21956d38321
ENV TZ="Europe/Oslo"
COPY target/oebs-mainmanager-api-*.jar app.jar
CMD ["-jar","app.jar"]