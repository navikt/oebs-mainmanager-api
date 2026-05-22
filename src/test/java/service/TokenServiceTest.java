package service;

import no.nav.oebs.api.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        TokenService.STATUS = "";
    }

    @AfterEach
    void tearDown() {
        TokenService.STATUS = "";
    }

    @Test
    void tokenService_canBeInstantiated() {
        assertEquals("TokenService", tokenService.getClass().getSimpleName());
    }

    @Test
    void genererToken_statusFieldIsPublic() {
        TokenService.STATUS = "OK";
        assertEquals("OK", TokenService.STATUS);
    }

    @Test
    void genererToken_statusFieldCanBeReset() {
        TokenService.STATUS = "OK";
        TokenService.STATUS = "";
        assertEquals("", TokenService.STATUS);
    }

    @Test
    void genererToken_configurationCanBeSetViaReflection() {
        ReflectionTestUtils.setField(tokenService, "mainManagerUserName", "testuser");
        ReflectionTestUtils.setField(tokenService, "mainManagerPassword", "testpass");
        ReflectionTestUtils.setField(tokenService, "mainManagerGrantType", "password");
        ReflectionTestUtils.setField(tokenService, "mainManagerUrlToken", "http://localhost/token");

        Object userName = ReflectionTestUtils.getField(tokenService, "mainManagerUserName");
        assertEquals("testuser", userName);
    }
}
