package no.nav.oebs.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.api.Application;
import no.nav.oebs.api.common.swagger.MainManagerSwagger;
import no.nav.oebs.api.service.ValiderKontoStrengService;
import no.nav.security.token.support.core.api.Protected;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.oebs.api.config.SwaggerConfig;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;


@Slf4j
@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.MAINMANAGER) //, description = "MAINMANAGER API")
public class ValiderKontoStreng {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private ValiderKontoStrengService validerKontoStrengService;

	public ValiderKontoStreng(ValiderKontoStrengService service) { //,
			this.validerKontoStrengService = service;
	}

	@Protected
	@GetMapping(path = "/validerkontostreng")
	@MainManagerSwagger
	public String finnValiderKontoStreng(
			@RequestParam(name = "org_id", defaultValue = "202") Integer org_id, //
			@RequestParam(name = "artskonto") @NotEmpty @Parameter(description = "eks. 669200000000") String artskonto,
			@RequestParam(name = "ksted")  @NotEmpty @Parameter(description = "f.eks. 243299") String ksted,
			@RequestParam(name = "produkt") @NotEmpty @Parameter(description = "f.eks. 000000") String produktoppgave,
			@RequestParam(name = "oppgave") @NotEmpty @Parameter(description = "f.eks. 000000") String deloppgave,
			@RequestParam(name = "felles") @NotEmpty @Parameter(description = "f.eks. L01002") String fellesoppgave,
			@RequestParam(name = "statskonto") @NotEmpty @Parameter(description = "f.eks. 266175000000") String statskonto,
			@RequestParam(name = "kilde") @NotEmpty @Parameter(description = "AP/AR") String kilde,
			@RequestParam(name = "tilsagnsaar") @NotEmpty @Parameter(description = "f.eks. 000000") String tilsagnsaar,
			@RequestParam(name = "fritt_felt_1") @NotEmpty @Parameter(description = "f.eks. 000000") String fritt_felt_1,
			@RequestParam(name = "fritt_felt_2") @NotEmpty @Parameter(description = "f.eks. 000000") String fritt_felt_2,
			@RequestParam(name = "fullmaktskode") @NotEmpty @Parameter(description = "f.eks. Z1") String fullmaktskode,
			@RequestParam(name = "regnskapsforer") @NotEmpty @Parameter(description = "f.eks. 80") String regnskapsforer,
			@RequestParam(name = "system", defaultValue = "MAINMANAGER") String system)

			{
					return validerKontoStrengService.finnValiderKontoStreng(org_id,
							artskonto,
							ksted,
							produktoppgave,
							deloppgave,
							fellesoppgave,
							statskonto,
							kilde,
							tilsagnsaar,
							fritt_felt_1,
							fritt_felt_2,
							fullmaktskode,
							regnskapsforer,
							system);
				}
}