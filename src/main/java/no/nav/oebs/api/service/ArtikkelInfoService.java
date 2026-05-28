package no.nav.oebs.api.service;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.api.common.model.ArtikkelRequest;
import no.nav.oebs.api.common.utils.ObjektMaps;
import no.nav.oebs.api.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.api.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;


@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class ArtikkelInfoService extends ObjektMaps {
    private static final String PLSQL_PROCEDURE = "xxrtv_mainmanager_api_pkg.xxrtv_artikkelinfo_api";

    private final PlsqlProcedureRepository plsqlProcedureRepository;

    public ArtikkelInfoService(PlsqlProcedureRepository plsqlProcedureRepository, JsonMapper objectMapper) {
        super(objectMapper);
        this.plsqlProcedureRepository = plsqlProcedureRepository;
    }

    public String finnArtikkelTransaksjoner(Integer orgid,String artikkelnavn, String artikkelnummer, LocalDate lastupdatedate) {

        PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(orgid, artikkelnavn, artikkelnummer, lastupdatedate));
        if (result.getMessageNumber() < 0) {
            throwPlsqlException(result);
        }

        return result.getData();
    }

    private ArtikkelRequest buildRequest(Integer orgid, String artikkelnavn, String artikkelnummer, LocalDate lastupdatedate) {
        return ArtikkelRequest.builder()
                .orgid(orgid) //
                .artikkelnavn(artikkelnavn) //
                .artikkelnummer(artikkelnummer) //
                .lastupdatedate(lastupdatedate) //
                .build();
    }

    /**
     * Kaller PL/SQL-prosedyren som utfører forretningslogikken til operasjonen.
     */
    private PlsqlProcedureResult executePlsqlProcedure(ArtikkelRequest request) {

        return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
    }
}