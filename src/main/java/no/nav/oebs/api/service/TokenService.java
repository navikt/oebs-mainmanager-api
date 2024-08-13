package no.nav.oebs.api.service;

import io.swagger.v3.oas.annotations.Parameter;
import no.nav.oebs.api.Application;
import no.nav.oebs.api.common.swagger.MainManagerSwagger;
//import no.nav.security.token.support.core.api.Protected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;

public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${mainmanager.username}")
    private String mainManagerUserName;

    @Value("${mainmanager.password}")
    private String mainManagerPassword;

    @Value("${mainmanager.grant_type}")
    private String mainManagerGrantType;

    @Value("${mainmanager.url.token}")
    private String mainManagerUrlToken;


    public String genererToken() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.set("username", mainManagerUserName);
        headers.set("password", mainManagerPassword);
        headers.set("grant_type", mainManagerGrantType);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(mainManagerUrlToken, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("200 OK: {}", response.getBody());
            return response.getBody();
        } else {
            logger.info("{}", response.getStatusCode());
            return "request failet";
        }
    }
}