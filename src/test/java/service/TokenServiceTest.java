package service;

import com.sun.net.httpserver.HttpServer;
import no.nav.oebs.api.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        TokenService.STATUS = "";

        ReflectionTestUtils.setField(tokenService, "mainManagerUserName", "testuser");
        ReflectionTestUtils.setField(tokenService, "mainManagerPassword", "testpass");
        ReflectionTestUtils.setField(tokenService, "mainManagerGrantType", "password");
    }

    @AfterEach
    void tearDown() {
        TokenService.STATUS = "";
    }

    @Test
    void genererToken_whenResponseIsOk_returnsAccessTokenAndSetsStatusOk() throws Exception {
        HttpServer server = startTokenServer(200, "{\"access_token\":\"token-123\"}");
        try {
            ReflectionTestUtils.setField(tokenService, "mainManagerUrlToken", tokenUrl(server));

            String token = tokenService.genererToken();

            assertEquals("token-123", token);
            assertEquals("OK", TokenService.STATUS);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void genererToken_whenResponseIsNotOk_returnsBody() throws Exception {
        // 401 causes RestTemplate to throw before TokenService reaches the non-OK branch.
        // Use 201 to exercise the else-branch without triggering HttpClientErrorException.
        HttpServer server = startTokenServer(201, "unauthorized");
        try {
            ReflectionTestUtils.setField(tokenService, "mainManagerUrlToken", tokenUrl(server));

            String response = tokenService.genererToken();

            assertEquals("unauthorized", response);
            assertEquals("", TokenService.STATUS);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void genererToken_whenResponseBodyIsInvalidJson_throwsException() throws Exception {
        HttpServer server = startTokenServer(200, "not-json");
        try {
            ReflectionTestUtils.setField(tokenService, "mainManagerUrlToken", tokenUrl(server));

            assertThrows(Exception.class, () -> tokenService.genererToken());
            assertEquals("", TokenService.STATUS);
        } finally {
            server.stop(0);
        }
    }

    private static HttpServer startTokenServer(int statusCode, String responseBody) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/token", exchange -> {
            byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response);
            }
        });
        server.start();
        return server;
    }

    private static String tokenUrl(HttpServer server) {
        return "http://localhost:" + server.getAddress().getPort() + "/token";
    }
}
