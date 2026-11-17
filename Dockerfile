FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:bf30ec70b3b5b51280dae1889e67d27e793166c814a7e5caca84713cb360a92c
ENV TZ="Europe/Oslo"
COPY target/oebs-mainmanager-api-*.jar app.jar
CMD ["-jar","app.jar"]