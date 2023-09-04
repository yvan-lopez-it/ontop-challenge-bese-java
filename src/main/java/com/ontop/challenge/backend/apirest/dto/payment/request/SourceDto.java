package com.ontop.challenge.backend.apirest.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SourceDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -7594859011395372976L;

    @JsonProperty("type")
    private String type;

    @JsonProperty("sourceInformation")
    private SourceInformationDto sourceInformation;

    @JsonProperty("account")
    private AccountDto account;
}
