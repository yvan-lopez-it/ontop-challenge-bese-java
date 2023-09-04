package com.ontop.challenge.backend.apirest.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SourceInformationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5216733056067799059L;

    @JsonProperty("name")
    private String name;
}
