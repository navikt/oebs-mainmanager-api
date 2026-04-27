# MainManager - OeBS API

REST API'er tilbudt av OeBS.

## Beskrivelse

MainManager OeBS API er en Spring Boot-basert REST API som tilbyr integrasjon med OeBS (Oracle e-Business Suite) systemer. APIet fungerer som en gateway mellom klienter og OeBS backend, og gir tilgang til ulike forretningsfunksjoner som artikkelinformasjon og validering av kontostrenger.

## Funksjonalitet

### Hovedfunksjoner

- **Artikkelinformasjon**: Hent transaksjonsdata for artikler med søk basert på organisasjons-ID, artikkelnavn, artikkelnummer og siste oppdateringsdato
- **Kontostrengvalidering**: Valider kontostrenger ved å sjekke kombinasjoner av økonomiske parametere som artskonto, koststed, produktoppgave, og andre dimensjoner
- **Kall-logging**: Automatisk logging av alle API-kall med korrelasjon-ID for sporing og debugging

## Teknologi

- **Java 11+** med Spring Boot
- **JPA/Hibernate** for databasetilgang
- **PL/SQL-integrasjon** med OeBS via Native Queries
- **JWT-autentisering** via Azure AD
- **Swagger/OpenAPI** for API-dokumentasjon
- **Docker** for containerisering

## Sikkerhet

APIet støtter følgende sikkerhetsmakansimer:

- **Azure AD JWT-tokens**: Bearer token autentisering basert på Azure AD
- **Basic Authentication**: Klassisk HTTP Basic Authentication
- **Token-validering**: Automatisk validering av innkommende tokens via `EnableJwtTokenValidation`

## API-Endepunkter

APIet tilbyr REST-endepunkter under `/api` som integrer med PL/SQL-prosedyrer i OeBS:

- Kall til `xxrtv_mainmanager_api_pkg.xxrtv_artikkelinfo_api` for artikkelinformasjon
- Kall til `xxrtv_mainmanager_api_pkg.xxrtv_validerkontostreng_api` for kontostrengvalidering

## Konfigurering

Følgende miljøvariabler benyttes:

- `OEBS_ENV`: Miljø-navn (dev, test, prod)
- `APP_VERSION`: Applikasjonsversjon
- `APP_UPDATE`: Siste oppdateringsdato

## Logging

Systemet logger alle HTTP-kall (request/response) til databasetabellen `XXRTV_MAINMANAGER_LOGG` med:
- Korrelasjon-ID for sporing av forespørsler
- Tidspunkt for kallet
- Type (PLSQL, REST)
- Retning (INN/UT)
- HTTP-metode og operasjon
- Response-status
- Kalltid
- Request- og response-data

## Lisens

MIT License