package no.nav.oebs.api.service;

import no.nav.oebs.api.Application;
import no.nav.oebs.api.config.ProxyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;

@Service
public class KonteringService {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    ProxyConfig proxyConfig;

    @Autowired
    KonteringsInfoGLService konteringsInfoGLService;

    @Autowired
    private TokenService tokenService;

    public String konteringsInfo(Integer org_id, String segmentname, String segmentverdi, LocalDate lastupdatedate, String mainManagerUrl) throws Exception {

         //String kont = konteringsInfoGLService.finnGLKonteringsInfoTransaksjoner(org_id, segmentname, segmentverdi, lastupdatedate);

        String tokenet = tokenService.genererToken();

        try {
            if (Objects.equals(TokenService.STATUS, "OK")) {

                //RestTemplate restTemplate = new RestTemplate(proxyConfig.requestFactory());

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.setBearerAuth(tokenet);

                String kont = konteringsInfoGLService.finnGLKonteringsInfoTransaksjoner(org_id, segmentname, segmentverdi, lastupdatedate);

                HttpEntity<String> entity = new HttpEntity<>(kont, headers);

                ResponseEntity<String> response = restTemplate.exchange(mainManagerUrl, HttpMethod.POST, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("200 OK: {}", response.getBody());
                    return response.getBody();
                } else {
                    logger.info("{}", response.getStatusCode());
                    return response.getBody();
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        logger.info("MainManager token feilet" );
        return null;
    }
}
