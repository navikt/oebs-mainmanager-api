package no.nav.oebs.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.oebs.api.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class TokenService {

    public static String STATUS = "";
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

// Set request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // headers.set("User-Agent", "Oebs-MainManger-api/0.0.4");

// Set request body parameters
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("username", mainManagerUserName);
        requestBody.add("password", mainManagerPassword);
        requestBody.add("grant_type", mainManagerGrantType);

// Make the POST request
        String url = mainManagerUrlToken;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestBody, String.class);

        try {
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                String jsonText = responseEntity.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonText);
                String accessToken = jsonNode.get("access_token").asText();

                STATUS = "OK";
                logger.info("Inside TokenService:  " + accessToken);
                return accessToken;
            }
        } catch (Exception e) {
           return responseEntity.getBody();
        }

        return null;
    }
}

