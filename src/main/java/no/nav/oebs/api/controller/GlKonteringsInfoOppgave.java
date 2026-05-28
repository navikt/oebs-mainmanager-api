package no.nav.oebs.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.nav.oebs.api.common.swagger.MainManagerSwagger;
import no.nav.oebs.api.config.SwaggerConfig;
import no.nav.oebs.api.service.KonteringService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.MAINMANAGER)
public class GlKonteringsInfoOppgave {

    @Value("${mainmanager.oppgave}")
    private String mainManagerOppgave;

    KonteringService konteringService;

    public GlKonteringsInfoOppgave(KonteringService konteringService) {
        this.konteringService = konteringService;
    }

    @Protected
    @PostMapping(path = "/gl_konteringsinfo_oppgave")
    @MainManagerSwagger
    public String glArtKontoTransaksjoner(
            @RequestParam(name = "org id", defaultValue = "202") Integer orgid,
            @RequestParam(name = "segmentverdi", required = false)
            @Parameter(description = "f.eks. W00001") String segmentverdi,
            @RequestParam(name = "lastupdatedate", defaultValue = "")
            @Parameter(description = "f.eks. 2022-12-25")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastupdatedate) {

        return konteringService.konteringsInfo(orgid, "OR_AKTIVITET", segmentverdi, lastupdatedate, mainManagerOppgave);

    }
}
