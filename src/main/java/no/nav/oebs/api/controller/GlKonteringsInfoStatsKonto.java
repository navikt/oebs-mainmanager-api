package no.nav.oebs.api.controller;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.nav.oebs.api.Application;
import no.nav.oebs.api.common.swagger.MainManagerSwagger;
import no.nav.oebs.api.config.SwaggerConfig;
import no.nav.oebs.api.service.KonteringService;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.MAINMANAGER) //, description = "MAINMANAGER API")
public class GlKonteringsInfoStatsKonto {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${MM_STATSREGNSKAPSKONTO}")
    private String mainManagerStatsKonto;

    @Autowired
    KonteringService konteringService;

    @Protected
    @PostMapping(path = "/gl_konteringsinfo_statskonto")
    @MainManagerSwagger
    //@SecurityRequirement(name = "basicAuth")
    public String glArtKontoTransaksjoner(
            @RequestParam(name = "org id", defaultValue = "202") Integer org_id,
            @RequestParam(name = "segmentverdi", required = false)
            @Parameter(description = "f.eks. 060501000000") String segmentverdi,
            @RequestParam(name = "lastupdatedate", defaultValue = "")
            @Parameter(description = "f.eks. 2022-12-25")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastupdatedate) throws Exception {

        return konteringService.konteringsInfo(org_id, "OR_STATSKONTO", segmentverdi, lastupdatedate, mainManagerStatsKonto);
    }
}
