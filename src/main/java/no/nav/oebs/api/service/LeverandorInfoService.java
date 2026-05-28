package no.nav.oebs.api.service;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.api.common.utils.ObjektMaps;
import no.nav.oebs.api.common.model.LevRequest;
import no.nav.oebs.api.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.api.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class LeverandorInfoService extends ObjektMaps {
	private static final String PLSQL_PROCEDURE = "xxrtv_mainmanager_api_pkg.xxrtv_leverandoerinfo_api";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public LeverandorInfoService(PlsqlProcedureRepository plsqlProcedureRepository, JsonMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	public String finnLeverandorTransaksjoner(Integer orgid, String leverandornavn,
											  String leverandornummer, String leverandorsted, LocalDate lastupdatedate) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(orgid, leverandornavn,
				leverandornummer, leverandorsted, lastupdatedate));
		 if (result.getMessageNumber() < 0) {
			 throwPlsqlException(result);
		}

		return result.getData();
	}

	private LevRequest buildRequest(Integer orgid, String leverandornavn,
												String leverandornummer, String leverandorsted, LocalDate lastupdatedate) {
		return LevRequest.builder()
				.orgid(orgid) //
				.leverandornavn(leverandornavn) //
				.leverandornummer(leverandornummer) //
				.leverandorsted(leverandorsted) //
				.lastupdatedate(lastupdatedate) //
				.build();
	}

	/**
	 * Kaller PL/SQL-prosedyren som utfører forretningslogikken til operasjonen.
     */
	private PlsqlProcedureResult executePlsqlProcedure(LevRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}
}
