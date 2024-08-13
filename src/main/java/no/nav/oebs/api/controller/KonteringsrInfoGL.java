package no.nav.oebs.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import no.nav.oebs.api.Application;
import no.nav.oebs.api.common.swagger.MainManagerSwagger;
import no.nav.oebs.api.service.KonteringsInfoGLService;
//import no.nav.security.token.support.core.api.Protected;
import no.nav.oebs.api.config.SwaggerConfig;
//import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.MAINMANAGER, description = "MAINMANAGER API")
public class KonteringsrInfoGL {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Value("${mainmanager.accounts}")
	private String mainManagerAccounts;

	private final KonteringsInfoGLService konteringsInfoGLService;

	public KonteringsrInfoGL(KonteringsInfoGLService serviceKont) { //,
		this.konteringsInfoGLService = serviceKont;
	}

	//@Unprotected
	@PostMapping(path = "/gl_konteringsinfo")
	@MainManagerSwagger
	public String konteringsInfoTransaksjoner(
			@RequestParam(name="org id", defaultValue = "202") Integer org_id,
			@RequestParam(name="segmentnavn", required = false)
				@Parameter(description = "f.eks. OR_AKTIVITET") String segmentname,
			@RequestParam(name="segmentverdi", required = false)
				@Parameter(description = "f.eks. W00006") String segmentverdi,
			@RequestParam(name = "lastupdatedate", defaultValue = "")
				@Parameter(description = "f.eks. 2022-12-25")
					@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastupdatedate) {

		String url = mainManagerAccounts;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String kont = konteringsInfoGLService.finnGLKonteringsInfoTransaksjoner(org_id, segmentname, segmentverdi, lastupdatedate);

		HttpEntity<String> entity = new HttpEntity<>(kont, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			logger.info("200 OK: {}", response.getBody());
			return kont;
		} else {
			logger.info("{}", response.getStatusCode());
			return "request failet";
		}
		//return konteringsInfoGLService.finnGLKonteringsInfoTransaksjoner(org_id, segmentname, segmentverdi, lastupdatedate);
	}
}