package no.nav.oebs.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.nav.oebs.api.common.swagger.MainManagerSwagger;
import no.nav.oebs.api.service.ArtikkelInfoService;
import no.nav.oebs.api.service.TokenService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.api.config.SwaggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.MAINMANAGER)
public class ArtikkelInfo {

    private static final Logger logger = LoggerFactory.getLogger(ArtikkelInfo.class);

    @Value("${mainmanager.artikkelinfo}")
    private String mainManagerArtikkelInfo;

    private final TokenService tokenService;

    private final ArtikkelInfoService artikkelInfoService;

    public ArtikkelInfo(TokenService tokenService, ArtikkelInfoService artikkelInfoService){
        this.tokenService = tokenService;
        this.artikkelInfoService = artikkelInfoService;
    }

    @Protected
    @PostMapping(path = "/artikkelinfo")
    @MainManagerSwagger
    public String finnArtikkelInfoTransaksjoner(
            @RequestParam(name = "org_id", defaultValue = "202") Integer orgid,
            @RequestParam(name = "artikkelnavn", required = false)
            @Parameter(description = "f.eks. artikkelnavn") String artikkelnavn,
            @RequestParam(name = "artikkelnummer", required = false)
            @Parameter(description = "f.eks. artikkelnummer") String artikkelnummer,
            @RequestParam(name = "lastupdatedate", defaultValue = "")
            @Parameter(description = "f.eks. 2024-09-12")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastupdatedate) {

        String token = tokenService.genererToken();

        if (Objects.equals(tokenService.getStatus(), "OK")) {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(token);

            String art = artikkelInfoService.finnArtikkelTransaksjoner(orgid, artikkelnavn,
                    artikkelnummer, lastupdatedate);
            HttpEntity<String> entity = new HttpEntity<>(art,headers);
            ResponseEntity<String> response = restTemplate.postForEntity(mainManagerArtikkelInfo, entity, String.class);

            if(response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.info("Response code:  {}", response.getStatusCode());
                return response.getBody();
            }
        }

        logger.info("MainManager token feilet" );
        return null;
    }
}
