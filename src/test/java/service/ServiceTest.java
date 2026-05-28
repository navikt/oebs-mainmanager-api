package service;

import no.nav.oebs.api.db.repository.PlsqlMessageCodes;
import no.nav.oebs.api.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.api.db.repository.PlsqlProcedureResult;
import no.nav.oebs.api.exception.TechnicalPlsqlException;
import no.nav.oebs.api.exception.UgyldigInputException;
import no.nav.oebs.api.service.ArtikkelInfoService;
import no.nav.oebs.api.service.KonteringService;
import no.nav.oebs.api.service.KonteringsInfoGLService;
import no.nav.oebs.api.service.LeverandorInfoService;
import no.nav.oebs.api.service.TokenService;
import no.nav.oebs.api.service.ValiderKontoStrengService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    private PlsqlProcedureRepository plsqlProcedureRepository;

    @Mock
    private JsonMapper jsonMapper;

    private PlsqlProcedureResult resultWithData(String data) {
        return new PlsqlProcedureResult(data, PlsqlMessageCodes.OK, "OK");
    }

    private PlsqlProcedureResult resultWithError(int messageNumber, String message) {
        return new PlsqlProcedureResult(null, messageNumber, message);
    }

    @Nested
    class ArtikkelInfoServiceTests {

        private ArtikkelInfoService service;

        @BeforeEach
        void setUp() {
            service = new ArtikkelInfoService(plsqlProcedureRepository, jsonMapper) {
                @Override
                protected <T> String toJson(T object) {
                    return "{}";
                }
            };
        }

        @Test
        void finnArtikkelTransaksjoner_returnsDataFromRepository() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("[{\"artikkelnummer\":\"3170085\"}]"));

            String result = service.finnArtikkelTransaksjoner(202, "Navn", "3170085", null);

            assertEquals("[{\"artikkelnummer\":\"3170085\"}]", result);
        }

        @Test
        void finnArtikkelTransaksjoner_withTechnicalError_throwsTechnicalPlsqlException() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithError(PlsqlMessageCodes.EXCEPTION, "DB error"));

            assertThrows(TechnicalPlsqlException.class,
                    () -> service.finnArtikkelTransaksjoner(202, "Navn", "3170085", null));
        }

        @Test
        void finnArtikkelTransaksjoner_withInputError_throwsUgyldigInputException() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithError(PlsqlMessageCodes.FEIL_I_INPUT, "Invalid input"));

            assertThrows(UgyldigInputException.class,
                    () -> service.finnArtikkelTransaksjoner(202, "Navn", "3170085", null));
        }

        @Test
        void finnArtikkelTransaksjoner_callsCorrectProcedure() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("[]"));

            service.finnArtikkelTransaksjoner(202, "Navn", "3170085", null);

            verify(plsqlProcedureRepository).executeInOutProcedure(
                    eq("xxrtv_mainmanager_api_pkg.xxrtv_artikkelinfo_api"), any());
        }
    }

    @Nested
    class KonteringsInfoGLServiceTests {

        private KonteringsInfoGLService service;

        @BeforeEach
        void setUp() {
            service = new KonteringsInfoGLService(plsqlProcedureRepository, jsonMapper) {
                @Override
                protected <T> String toJson(T object) {
                    return "{}";
                }
            };
        }

        @Test
        void finnGLKonteringsInfoTransaksjoner_returnsDataFromRepository() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("[{\"segment\":\"data\"}]"));

            String result = service.finnGLKonteringsInfoTransaksjoner(202, "KSTED", "1234", null);

            assertEquals("[{\"segment\":\"data\"}]", result);
        }

        @Test
        void finnGLKonteringsInfoTransaksjoner_callsCorrectProcedure() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("[]"));

            service.finnGLKonteringsInfoTransaksjoner(202, "KSTED", "1234", null);

            verify(plsqlProcedureRepository).executeInOutProcedure(
                    eq("xxrtv_mainmanager_api_pkg.xxrtv_gl_konteringsinfo_api"), any());
        }
    }

    @Nested
    class LeverandorInfoServiceTests {

        private LeverandorInfoService service;

        @BeforeEach
        void setUp() {
            service = new LeverandorInfoService(plsqlProcedureRepository, jsonMapper) {
                @Override
                protected <T> String toJson(T object) {
                    return "{}";
                }
            };
        }

        @Test
        void finnLeverandorTransaksjoner_returnsDataFromRepository() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("[{\"leverandor\":\"Navn AS\"}]"));

            String result = service.finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", null);

            assertEquals("[{\"leverandor\":\"Navn AS\"}]", result);
        }

        @Test
        void finnLeverandorTransaksjoner_withInputError_throwsUgyldigInputException() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithError(PlsqlMessageCodes.FEIL_I_INPUT, "Invalid input"));

            assertThrows(UgyldigInputException.class,
                    () -> service.finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", null));
        }

        @Test
        void finnLeverandorTransaksjoner_withTechnicalError_throwsTechnicalPlsqlException() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithError(PlsqlMessageCodes.EXCEPTION, "DB error"));

            assertThrows(TechnicalPlsqlException.class,
                    () -> service.finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", null));
        }

        @Test
        void finnLeverandorTransaksjoner_callsCorrectProcedure() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("[]"));

            service.finnLeverandorTransaksjoner(202, "Navn AS", "12345", "Oslo", null);

            verify(plsqlProcedureRepository).executeInOutProcedure(
                    eq("xxrtv_mainmanager_api_pkg.xxrtv_leverandoerinfo_api"), any());
        }
    }

    @Nested
    class ValiderKontoStrengServiceTests {

        private ValiderKontoStrengService service;

        @BeforeEach
        void setUp() {
            service = new ValiderKontoStrengService(plsqlProcedureRepository, jsonMapper) {
                @Override
                protected <T> String toJson(T object) {
                    return "{}";
                }
            };
        }

        @Test
        void finnValiderKontoStreng_returnsDataFromRepository() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("{\"gyldig\":true}"));

            String result = service.finnValiderKontoStreng(
                    202, "4900", "1234", null, null, null, null, null, null, null, null, null, null, null);

            assertEquals("{\"gyldig\":true}", result);
        }

        @Test
        void finnValiderKontoStreng_callsCorrectProcedure() {
            when(plsqlProcedureRepository.executeInOutProcedure(any(), any()))
                    .thenReturn(resultWithData("[]"));

            service.finnValiderKontoStreng(
                    202, "4900", "1234", "PROD", "DEL", "FELLES",
                    "STATS", "KILDE", "2024", "FF1", "FF2", "FULL", "REG", "SYS");

            verify(plsqlProcedureRepository).executeInOutProcedure(
                    eq("xxrtv_mainmanager_api_pkg.xxrtv_validerkontostreng_api"), any());
        }
    }

    @Nested
    class KonteringServiceTests {

        @Mock
        private KonteringsInfoGLService konteringsInfoGLService;

        @Mock
        private TokenService tokenService;

        private KonteringService service;

        @BeforeEach
        void setUp() {
            service = new KonteringService(konteringsInfoGLService, tokenService);
            ReflectionTestUtils.setField(service, "konteringsInfoGLService", konteringsInfoGLService);
            ReflectionTestUtils.setField(service, "tokenService", tokenService);
        }

        @Test
        void konteringsInfo_whenTokenStatusIsNotOk_returnsNull(){
            when(tokenService.genererToken()).thenReturn("token");
            when(tokenService.getStatus()).thenReturn("FAIL");

            String result = service.konteringsInfo(202, "KSTED", "1234", LocalDate.now(), "http://localhost");

            assertNull(result);
        }

        @Test
        void konteringsInfo_whenDependencyThrows_returnsExceptionMessage() {
            when(tokenService.genererToken()).thenReturn("token");
            when(tokenService.getStatus()).thenReturn("OK");
            when(konteringsInfoGLService.finnGLKonteringsInfoTransaksjoner(any(), any(), any(), any()))
                    .thenThrow(new RuntimeException("GL service failed"));

            String result = service.konteringsInfo(202, "KSTED", "1234", LocalDate.now(), "http://localhost");

            assertEquals("GL service failed", result);
        }
    }
}
