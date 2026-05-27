package no.nav.oebs.api.common.utils;


import no.nav.oebs.api.db.repository.PlsqlMessageCodes;
import no.nav.oebs.api.db.repository.PlsqlProcedureResult;
import no.nav.oebs.api.exception.JsonMappingException;
import no.nav.oebs.api.exception.TechnicalPlsqlException;
import no.nav.oebs.api.exception.UgyldigInputException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Superklasse med felles funksjonalitet for implementasjon av tjenestespesifikke Service-klasser.
 */
public class ObjektMaps {

	private final JsonMapper jsonMapper;

	protected ObjektMaps(JsonMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	/**
	 * Kaster exception iht. feilkoden returnert fra PL/SQL-prosedyren.
	 */
	protected void throwPlsqlException(PlsqlProcedureResult result) {
		switch (result.getMessageNumber()) {
		case PlsqlMessageCodes.FEIL_I_INPUT:
			throw new UgyldigInputException(result.getMessage());
		default:
			throw new TechnicalPlsqlException(result.getMessageNumber(), result.getMessage());
		}
	}

	/**
	 * Mapper fra Java- til JSON-objekt.
	 */
	protected <T> String toJson(T object) {
		try {
			return jsonMapper.writeValueAsString(object);
		} catch (JacksonException e) {
			throw new JsonMappingException(e);
		}
	}

	/**
	 * Mapper fra JSON- til Java-objekt.
	 */
	protected <T> T toObject(String json, Class<T> valueType) {
		try {
			return jsonMapper.readValue(json, valueType);
		} catch (JacksonException e) {
			throw new JsonMappingException(e);
		}
	}

}
