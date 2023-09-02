package com.ontop.challenge.backend.apirest.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class AccountDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -3728154517634312985L;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("routingNumber")
    private String routingNumber;
}
