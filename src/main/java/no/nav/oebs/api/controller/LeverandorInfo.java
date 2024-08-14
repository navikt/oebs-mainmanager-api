package no.nav.oebs.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.api.Application;
import no.nav.oebs.api.common.swagger.MainManagerSwagger;
import no.nav.oebs.api.service.LeverandorInfoService;
import no.nav.oebs.api.service.TokenService;
//import no.nav.security.token.support.core.api.Protected;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.oebs.api.config.SwaggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.MAINMANAGER, description = "MAINMANAGER API")
public class LeverandorInfo {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Value("${mainmanager.vendors}")
	private String mainManagerVendors;

	private final TokenService tokenService;
	private final LeverandorInfoService leverandorInfoService;
	public LeverandorInfo(TokenService tokenService, LeverandorInfoService serviceLev) {
        this.tokenService = tokenService; //,
		this.leverandorInfoService = serviceLev;
	}

	//@Protected
	@PostMapping(path = "/leverandorinfo")
	@MainManagerSwagger
	public String finnLeverandortransaksjoner(
			@RequestParam(name="org id", defaultValue = "202") Integer org_id,
			@RequestParam(name="leverandornavn", required = false)
				@Parameter(description = "f.eks. BOUVET ASA") String leverandornavn,
			@RequestParam(name = "leverandornummer", required = false)
				@Parameter(description = "f.eks. 7048") String leverandornummer,
			@RequestParam(name = "leverandorsted", required = false)
				@Parameter(description = "f.eks. NYDALEN") String leverandorsted,
			@RequestParam(name = "lastupdatedate", defaultValue = "")
				@Parameter(description = "f.eks. 2022-10-25")
					@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastupdatedate) {


		String tokenet = tokenService.genererToken();

		if (Objects.equals(TokenService.STATUS, "OK")) {

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			String lev =  leverandorInfoService.finnLeverandorTransaksjoner(org_id, leverandornavn, leverandornummer, leverandorsted, lastupdatedate);

			HttpEntity<String> entity = new HttpEntity<>(lev, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(mainManagerVendors, entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				logger.info("200 OK: {}", response.getBody());
				return lev;
			} else {
				logger.info("{}", response.getStatusCode());
				return "request failet";
			}
		}
		return null;
	}
}
