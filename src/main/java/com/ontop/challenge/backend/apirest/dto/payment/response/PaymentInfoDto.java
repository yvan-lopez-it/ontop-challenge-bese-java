package com.ontop.challenge.backend.apirest.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class PaymentInfoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 2821600532399929596L;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("id")
    private String id;
}
