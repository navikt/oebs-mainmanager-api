package no.nav.oebs.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "org_id", "artikkelname", "artikkelnummer", "lastupdatedate" })
public class ArtikkelRequest {

    @JsonProperty("org_id")
    private Integer orgid;

    private String artikkelnavn;

    private String artikkelnummer;

    private LocalDate lastupdatedate;
}
