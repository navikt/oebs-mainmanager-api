FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:e90bc511f565348d5f7b8f15b0f4a4f2fd38ffc34a7a3d33a4cd00202afb5e51
ENV TZ="Europe/Oslo"
COPY target/oebs-mainmanager-api-*.jar app.jar
CMD ["-jar","app.jar"]