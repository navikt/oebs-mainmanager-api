FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:3f6b44cffd97ead2970ec1e64ff4eb9f341b13c897f48d3ddbd2e1eb65a3a7a1
ENV TZ="Europe/Oslo"
COPY target/oebs-mainmanager-api-*.jar app.jar
CMD ["-jar","app.jar"]