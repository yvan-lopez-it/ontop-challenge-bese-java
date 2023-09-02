package com.ontop.challenge.backend.apirest.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class PaymentRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 8337207211411948430L;

    @JsonProperty("source")
    private SourceDto source;

    @JsonProperty("destination")
    private DestinationDto destination;

    @JsonProperty("amount")
    private Double amount;
}
