package no.nav.oebs.api.service;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.api.common.utils.ObjektMaps;
import no.nav.oebs.api.common.model.KontRequest;
import no.nav.oebs.api.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.api.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class KonteringsInfoGLService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_mainmanager_api_pkg.xxrtv_gl_konteringsinfo_api";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public KonteringsInfoGLService(PlsqlProcedureRepository plsqlProcedureRepository, JsonMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}
	public String finnGLKonteringsInfoTransaksjoner(Integer orgid, String segmentname, String segmentverdi, LocalDate lastupdatedate ) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(orgid, segmentname, segmentverdi, lastupdatedate));
		return result.getData();

	}
	private KontRequest buildRequest(Integer orgid, String segmentname, String segmentverdi, LocalDate lastupdatedate) {
		return KontRequest.builder()
				.orgid(orgid) //
				.segmentname(segmentname) //
				.segmentverdi(segmentverdi) //
				.lastupdatedate(lastupdatedate) //
				.build();
	}
	private PlsqlProcedureResult executePlsqlProcedure(KontRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}
}
