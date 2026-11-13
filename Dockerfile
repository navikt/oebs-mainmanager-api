FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:0958f3b681a16bdcb3e4a481d5dbc857dafbbe4be15a0cd2eeebf7fc70fe22f7
ENV TZ="Europe/Oslo"
COPY target/oebs-mainmanager-api-*.jar app.jar
CMD ["-jar","app.jar"]