package com.ontop.challenge.backend.apirest.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DestinationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 6155937893794421483L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("account")
    private AccountDto account;

}
