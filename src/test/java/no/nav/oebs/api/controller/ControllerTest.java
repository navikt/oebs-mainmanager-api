package no.nav.oebs.api.controller;

import no.nav.oebs.api.service.ArtikkelInfoService;
import no.nav.oebs.api.service.LeverandorInfoService;
import no.nav.oebs.api.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @AfterEach
    void resetTokenStatus() {
        TokenService.STATUS = "";
    }

    @Nested
    class ArtikkelInfoControllerTests {

        @Mock
        private TokenService tokenService;

        @Mock
        private ArtikkelInfoService artikkelInfoService;

        private ArtikkelInfo controller;

        @BeforeEach
        void setUp() {
            controller = new ArtikkelInfo(tokenService, artikkelInfoService);
        }

        @Test
        void finnArtikkelInfoTransaksjoner_whenTokenStatusIsNotOk_returnsNull() throws Exception {
            when(tokenService.genererToken()).thenReturn("token");
            TokenService.STATUS = "FAIL";

            String result = controller.finnArtikkelInfoTransaksjoner(
                    202, "artikkelnavn", "3170085", LocalDate.now());

            assertNull(result);
            verify(artikkelInfoService, never())
                    .finnArtikkelTransaksjoner(202, "artikkelnavn", "3170085", LocalDate.now());
        }

        @Test
        void finnArtikkelInfoTransaksjoner_whenTokenServiceThrows_propagatesException() throws Exception {
            when(tokenService.genererToken()).thenThrow(new RuntimeException("Token error"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> controller.finnArtikkelInfoTransaksjoner(202, null, null, null));

            assertEquals("Token error", exception.getMessage());
        }
    }

    @Nested
    class LeverandorInfoControllerTests {

        @Mock
        private TokenService tokenService;

        @Mock
        private LeverandorInfoService leverandorInfoService;

        private LeverandorInfo controller;

        @BeforeEach
        void setUp() {
            controller = new LeverandorInfo(tokenService, leverandorInfoService);
        }

        @Test
        void finnLeverandortransaksjoner_whenTokenStatusIsNotOk_returnsNull() throws Exception {
            when(tokenService.genererToken()).thenReturn("token");
            TokenService.STATUS = "FAIL";

            String result = controller.finnLeverandortransaksjoner(
                    202, "Navn AS", "12345", "Oslo", LocalDate.now());

            assertNull(result);
            verify(leverandorInfoService, never())
                    .finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", LocalDate.now());
        }

        @Test
        void finnLeverandortransaksjoner_whenTokenServiceThrows_propagatesException() throws Exception {
            when(tokenService.genererToken()).thenThrow(new RuntimeException("Token error"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> controller.finnLeverandortransaksjoner(202, null, null, null, null));

            assertEquals("Token error", exception.getMessage());
        }
    }
}
