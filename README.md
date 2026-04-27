# MainManager - Oebs Api

REST API'er tilbudt av Oebs.

## Beskrivelse

OEBS MainManager API er en Spring Boot-basert REST API som fungerer som integrasjonsgrensesnitt mot Oracle E-Business Suite (EBS). APIet tilbyr følgende hovedfunksjoner:

### Hovedfunksjoner

- **Artikkelinformasjon**: Hent transaksjonsinformasjon for artikler basert på organisasjons-ID, artikkelnavn, artikkeltnummer og oppdateringsdato
- **Kontostrenvalidering**: Valider komplekse kontostreneer med støtte for multiple kontodimensjoner (artskonto, koststed, produktoppgave, etc.)
- **Kall-logging**: Automatisk logging av alle innkommende og utgående API-kall med full request/response-historikk
- **OpenAPI/Swagger-dokumentasjon**: Interaktiv API-dokumentasjon tilgjengelig via web-grensesnitt

### Teknologi

- **Java 21** og Spring Boot 3.4.3
- **JPA/Hibernate** for databasekommunikasjon
- **Oracle Database** (JDBC driver)
- **JWT-autentisering** via Azure AD
- **Prometheus-metrikker** for overvåking
- **Logstash-integrasjon** for log-aggregering og Kibana
- **Jetty** som embedded web-container

## Sikkerhet

- API-et krever aksesstoken utstedt av Azure AD
- JWT-tokens valideres automatisk på alle `/api/*`-endepunkter
- Støtter både Bearer-token (OAuth 2.0) og Basic Authentication

## Installasjon

### Forutsetninger

- Java 21 eller høyere
- Maven 3.8+
- Oracle Database-tilkobling (konfigureres via miljøvariabler)

### Bygge prosjektet

```bash
# Klon repositoriet
git clone https://github.com/navikt/oebs-mainmanager-api.git
cd oebs-mainmanager-api

# Bygg prosjektet
mvn clean package
```

## Kjøring

### Lokalt (utvikling)

```bash
# Start applikasjonen med spring-boot-maven-plugin
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Eller kjør JAR-filen direkte
java -jar target/oebs-mainmanager-api-0.4.6.jar
```

Applikasjonen starter på `http://localhost:8080` (eller den konfigurerte porten).

### Miljøvariabler

```bash
# Påkrevd
OEBS_ENV=DEV|TEST|PROD              # Miljø
APP_VERSION=0.4.6                    # Applikasjonsversjon
APP_UPDATE=2026-04-27                # Oppfriskningsdato

# Database
spring.datasource.url=jdbc:oracle:thin:@host:port:sid
spring.datasource.username=oebsuser
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect

# Azure AD (autentisering)
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/{tenant}/v2.0
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://login.microsoftonline.com/{tenant}/discovery/v2.0/keys
```

### Endepunkter

API-et er tilgjengelig på følgende URL-er etter oppstart:

- **Swagger/OpenAPI-dokumentasjon**: `https://oebs-mainmanager.nav.no/swagger-ui/index.html#`
- **API-dokumetasjon (JSON)**: `https://oebs-mainmanager.nav.no/v3/api-docs`
- **Health-sjekk**: `https://oebs-mainmanager.nav.no/actuator/health`
- **Prometheus-metrikker**: `https://oebs-mainmanager.nav.no/actuator/prometheus`

## Docker

### Bygge Docker-image

```bash
# Bygg JAR-filen først
mvn clean package

# Bygg Docker-image
docker build -t navikt/oebs-mainmanager-api:latest .

# Med spesifikk tag/versjon
docker build -t navikt/oebs-mainmanager-api:0.4.6 .
```

### Kjøre Docker-container

```bash
# Basis-kjøring
docker run -p 8080:8080 \
  -e spring.datasource.url=jdbc:oracle:thin:@oracle-host:1521:OEBS \
  -e spring.datasource.username=oebsuser \
  -e spring.datasource.password=password \
  -e OEBS_ENV=DEV \
  navikt/oebs-mainmanager-api:latest

# Med volumes for logging
docker run -p 8080:8080 \
  -v /var/log/oebs:/app/logs \
  -e spring.datasource.url=jdbc:oracle:thin:@oracle-host:1521:OEBS \
  -e spring.datasource.username=oebsuser \
  -e spring.datasource.password=password \
  -e OEBS_ENV=DEV \
  navikt/oebs-mainmanager-api:latest
```

### Docker Compose (for lokal utvikling)

Opprett `docker-compose.yml`:

```yaml
version: '3.8'

services:
  oebs-api:
    build: .
    ports:
      - "8080:8080"
    environment:
      spring.datasource.url: jdbc:oracle:thin:@oracle-db:1521:OEBS
      spring.datasource.username: oebsuser
      spring.datasource.password: password
      OEBS_ENV: DEV
      APP_VERSION: 0.4.6
      APP_UPDATE: 2026-04-27
      TZ: Europe/Oslo
    depends_on:
      - oracle-db

  oracle-db:
    image: container-registry.oracle.com/database/enterprise:latest
    environment:
      ORACLE_PWD: password
    volumes:
      - oracle-data:/var/opt/oracle
    ports:
      - "1521:1521"

volumes:
  oracle-data:
```

Start med: `docker-compose up -d`

## Deployment

### Deploy til GCP Cloud Run

```bash
# Autentiser mot Google Cloud
gcloud auth login

# Bygge og push image
gcloud builds submit --tag gcr.io/PROJECT_ID/oebs-mainmanager-api

# Deploy til Cloud Run
gcloud run deploy oebs-mainmanager-api \
  --image gcr.io/PROJECT_ID/oebs-mainmanager-api \
  --platform managed \
  --region europe-north1 \
  --memory 1Gi \
  --cpu 1 \
  --set-env-vars OEBS_ENV=PROD,APP_VERSION=0.4.6 \
  --vpc-connector oracle-connector
```

### Deploy til Kubernetes

Opprett `k8s-deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oebs-mainmanager-api
  labels:
    app: oebs-mainmanager-api
spec:
  replicas: 2
  selector:
    matchLabels:
      app: oebs-mainmanager-api
  template:
    metadata:
      labels:
        app: oebs-mainmanager-api
    spec:
      containers:
      - name: api
        image: navikt/oebs-mainmanager-api:0.4.6
        ports:
        - containerPort: 8080
        env:
        - name: OEBS_ENV
          value: "PROD"
        - name: APP_VERSION
          value: "0.4.6"
        - name: spring.datasource.url
          valueFrom:
            secretKeyRef:
              name: oebs-db-config
              key: url
        - name: spring.datasource.username
          valueFrom:
            secretKeyRef:
              name: oebs-db-config
              key: username
        - name: spring.datasource.password
          valueFrom:
            secretKeyRef:
              name: oebs-db-config
              key: password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: oebs-mainmanager-api
spec:
  selector:
    app: oebs-mainmanager-api
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

Deploy med: `kubectl apply -f k8s-deployment.yaml`

## Overvåking

### Health-sjekker

```bash
# Liveness-probe
curl http://localhost:8080/actuator/health

# Readiness-probe
curl http://localhost:8080/actuator/health/readiness
```

### Metrikker

Prometheus-metrikker er tilgjengelig på `http://localhost:8080/actuator/prometheus`

### Logging

Alle API-kall logges automatisk til databasen (`XXRTV_MAINMANAGER_LOGG`-tabellen). Loggene inkluderer:
- Korrelasjon-ID for sporing
- Request/response-innhold
- HTTP-statuskoder
- Kalltider
- Feilmeldinger

## Lisens

MIT License