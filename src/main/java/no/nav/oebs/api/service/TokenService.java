package no.nav.oebs.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;


@Service
public class TokenService {

    public static String STATUS = "";

    @Value("${mainmanager.user}")
    private String mainManagerUserName;

    @Value("${mainmanager.password}")
    private String mainManagerPassword;

    @Value("${mainmanager.grant_type}")
    private String mainManagerGrantType;

    @Value("${mainmanager.url_token}")
    private String mainManagerUrlToken;

    public String genererToken() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("username", mainManagerUserName);
        requestBody.add("password", mainManagerPassword);
        requestBody.add("grant_type", mainManagerGrantType);

        String url = mainManagerUrlToken;

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestBody, String.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            String jsonText = responseEntity.getBody();
            JsonMapper objectMapper = new JsonMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);
            String accessToken = jsonNode.get("access_token").asString();

            STATUS = "OK";
            return accessToken;
        }
        else {
            return responseEntity.getBody();
        }
    }
}

