package no.nav.oebs.api.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;

@Service
@AllArgsConstructor
public class KonteringService {

    private static final Logger logger = LoggerFactory.getLogger(KonteringService.class);

    private final KonteringsInfoGLService konteringsInfoGLService;
    private final TokenService tokenService;

    public String konteringsInfo(Integer orgid, String segmentname, String segmentverdi, LocalDate lastupdatedate, String mainManagerUrl) {

        String tokenet = tokenService.genererToken();

        try {
            if (Objects.equals(tokenService.getStatus(), "OK")) {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.setBearerAuth(tokenet);

                String kont = konteringsInfoGLService.finnGLKonteringsInfoTransaksjoner(orgid, segmentname, segmentverdi, lastupdatedate);

                HttpEntity<String> entity = new HttpEntity<>(kont, headers);

                ResponseEntity<String> response = restTemplate.exchange(mainManagerUrl, HttpMethod.POST, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
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
