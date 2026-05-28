package no.nav.oebs.api.common.utils;

import no.nav.oebs.api.db.repository.PlsqlMessageCodes;
import no.nav.oebs.api.db.repository.PlsqlProcedureResult;
import no.nav.oebs.api.exception.JsonMappingException;
import no.nav.oebs.api.exception.TechnicalPlsqlException;
import no.nav.oebs.api.exception.UgyldigInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMapsTest {

    // Concrete subclass to expose protected methods for testing
    private static class TestObjektMaps extends ObjektMaps {
        TestObjektMaps(JsonMapper jsonMapper) {
            super(jsonMapper);
        }

        @Override
        public void throwPlsqlException(PlsqlProcedureResult result) {
            super.throwPlsqlException(result);
        }

        @Override
        public <T> String toJson(T object) {
            return super.toJson(object);
        }

        @Override
        public <T> T toObject(String json, Class<T> valueType) {
            return super.toObject(json, valueType);
        }

    }

    private TestObjektMaps objektMaps;

    @BeforeEach
    void setUp() {
        objektMaps = new TestObjektMaps(new JsonMapper());
    }

    @Nested
    class ThrowPlsqlExceptionTests {

        @Test
        void throwPlsqlException_withFeilIInput_throwsUgyldigInputException() {
            PlsqlProcedureResult result = new PlsqlProcedureResult(
                    null, PlsqlMessageCodes.FEIL_I_INPUT, "Invalid input");

            assertThrows(UgyldigInputException.class, () ->
                    objektMaps.throwPlsqlException(result));
        }

        @Test
        void throwPlsqlException_withOtherErrorCode_throwsTechnicalPlsqlException() {
            PlsqlProcedureResult result = new PlsqlProcedureResult(
                    null, PlsqlMessageCodes.EXCEPTION, "Technical error");

            assertThrows(TechnicalPlsqlException.class, () ->
                    objektMaps.throwPlsqlException(result));
        }

        @Test
        void throwPlsqlException_withUgyldigInputException_containsMessage() {
            String errorMessage = "Bad input value";
            PlsqlProcedureResult result = new PlsqlProcedureResult(
                    null, PlsqlMessageCodes.FEIL_I_INPUT, errorMessage);

            UgyldigInputException ex = assertThrows(UgyldigInputException.class, () ->
                    objektMaps.throwPlsqlException(result));

            assertTrue(ex.getMessage().contains(errorMessage));
        }
    }

    @Nested
    class ToJsonTests {

        @Test
        void toJson_withSimpleObject_returnsJsonString() {
            Map<String, String> object = Map.of("key", "value");

            String json = objektMaps.toJson(object);

            assertNotNull(json);
            assertTrue(json.contains("key"));
            assertTrue(json.contains("value"));
        }

        @Test
        void toJson_withNull_returnsNullJson() {
            String json = objektMaps.toJson(null);

            assertEquals("null", json);
        }

    }

    @Nested
    class ToObjectWithClassTests {

        @Test
        void toObject_withValidJson_returnsCorrectObject() {
            String json = "{\"name\":\"test\"}";

            Map result = objektMaps.toObject(json, Map.class);

            assertNotNull(result);
            assertEquals("test", result.get("name"));
        }

        @Test
        void toObject_withInvalidJson_throwsJsonMappingException() {
            assertThrows(JsonMappingException.class, () ->
                    objektMaps.toObject("not-valid-json", Map.class));
        }
    }

}
