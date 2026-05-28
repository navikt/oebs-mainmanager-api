package no.nav.oebs.api.controller;

import com.sun.net.httpserver.HttpServer;
import no.nav.oebs.api.service.ArtikkelInfoService;
import no.nav.oebs.api.service.LeverandorInfoService;
import no.nav.oebs.api.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

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
        void finnArtikkelInfoTransaksjoner_whenTokenStatusIsNotOk_returnsNull() {
            LocalDate date = LocalDate.of(2026, 1, 1);
            when(tokenService.genererToken()).thenReturn("token");
            when(tokenService.getStatus()).thenReturn("FAIL");

            String result = controller.finnArtikkelInfoTransaksjoner(202, "artikkelnavn", "3170085", date);

            assertNull(result);
            verify(artikkelInfoService, never())
                    .finnArtikkelTransaksjoner(202, "artikkelnavn", "3170085", date);
        }

        @Test
        void finnArtikkelInfoTransaksjoner_whenTokenServiceThrows_propagatesException() {
            when(tokenService.genererToken()).thenThrow(new RuntimeException("Token error"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> controller.finnArtikkelInfoTransaksjoner(202, null, null, null));

            assertEquals("Token error", exception.getMessage());
        }

        @Test
        void finnArtikkelInfoTransaksjoner_whenTokenOkAndMainManagerReturns200_returnsBody() throws Exception {
            LocalDate date = LocalDate.of(2026, 1, 2);
            HttpServer server = startServer(200, "mm-ok");
            try {
                ReflectionTestUtils.setField(controller, "mainManagerArtikkelInfo", serverUrl(server, "/endpoint"));
                when(tokenService.genererToken()).thenReturn("token");
                when(tokenService.getStatus()).thenReturn("OK");
                when(artikkelInfoService.finnArtikkelTransaksjoner(202, "artikkelnavn", "3170085", date))
                        .thenReturn("{\"payload\":true}");

                String result = controller.finnArtikkelInfoTransaksjoner(202, "artikkelnavn", "3170085", date);

                assertEquals("mm-ok", result);
                verify(artikkelInfoService).finnArtikkelTransaksjoner(202, "artikkelnavn", "3170085", date);
            } finally {
                server.stop(0);
            }
        }

        @Test
        void finnArtikkelInfoTransaksjoner_whenTokenOkAndMainManagerReturnsNon200_returnsBody() throws Exception {
            LocalDate date = LocalDate.of(2026, 1, 3);
            HttpServer server = startServer(201, "mm-created");
            try {
                ReflectionTestUtils.setField(controller, "mainManagerArtikkelInfo", serverUrl(server, "/endpoint"));
                when(tokenService.genererToken()).thenReturn("token");
                when(tokenService.getStatus()).thenReturn("OK");
                when(artikkelInfoService.finnArtikkelTransaksjoner(202, "artikkelnavn", "3170085", date))
                        .thenReturn("{\"payload\":true}");

                String result = controller.finnArtikkelInfoTransaksjoner(202, "artikkelnavn", "3170085", date);

                assertEquals("mm-created", result);
                verify(artikkelInfoService).finnArtikkelTransaksjoner(202, "artikkelnavn", "3170085", date);
            } finally {
                server.stop(0);
            }
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
        void finnLeverandortransaksjoner_whenTokenStatusIsNotOk_returnsNull() {
            LocalDate date = LocalDate.of(2026, 2, 1);
            when(tokenService.genererToken()).thenReturn("token");
            when(tokenService.getStatus()).thenReturn("FAIL");

            String result = controller.finnLeverandortransaksjoner(202, "Navn AS", "12345", "Oslo", date);

            assertNull(result);
            verify(leverandorInfoService, never())
                    .finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", date);
        }

        @Test
        void finnLeverandortransaksjoner_whenTokenServiceThrows_propagatesException() {
            when(tokenService.genererToken()).thenThrow(new RuntimeException("Token error"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> controller.finnLeverandortransaksjoner(202, null, null, null, null));

            assertEquals("Token error", exception.getMessage());
        }

        @Test
        void finnLeverandortransaksjoner_whenTokenOkAndMainManagerReturns200_returnsBody() throws Exception {
            LocalDate date = LocalDate.of(2026, 2, 2);
            HttpServer server = startServer(200, "mm-vendor-ok");
            try {
                ReflectionTestUtils.setField(controller, "mainManagerVendors", serverUrl(server, "/endpoint"));
                when(tokenService.genererToken()).thenReturn("token");
                when(tokenService.getStatus()).thenReturn("OK");
                when(leverandorInfoService.finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", date))
                        .thenReturn("{\"vendor\":true}");

                String result = controller.finnLeverandortransaksjoner(202, "Navn AS", "12345", "Oslo", date);

                assertEquals("mm-vendor-ok", result);
                verify(leverandorInfoService).finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", date);
            } finally {
                server.stop(0);
            }
        }

        @Test
        void finnLeverandortransaksjoner_whenTokenOkAndMainManagerReturnsNon200_returnsBody() throws Exception {
            LocalDate date = LocalDate.of(2026, 2, 3);
            HttpServer server = startServer(201, "mm-vendor-created");
            try {
                ReflectionTestUtils.setField(controller, "mainManagerVendors", serverUrl(server, "/endpoint"));
                when(tokenService.genererToken()).thenReturn("token");
                when(tokenService.getStatus()).thenReturn("OK");
                when(leverandorInfoService.finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", date))
                        .thenReturn("{\"vendor\":true}");

                String result = controller.finnLeverandortransaksjoner(202, "Navn AS", "12345", "Oslo", date);

                assertEquals("mm-vendor-created", result);
                verify(leverandorInfoService).finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", date);
            } finally {
                server.stop(0);
            }
        }
    }

    private static HttpServer startServer(int statusCode, String body) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/endpoint", exchange -> {
            byte[] response = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response);
            }
        });
        server.start();
        return server;
    }

    private static String serverUrl(HttpServer server, String path) {
        return "http://localhost:" + server.getAddress().getPort() + path;
    }
}
