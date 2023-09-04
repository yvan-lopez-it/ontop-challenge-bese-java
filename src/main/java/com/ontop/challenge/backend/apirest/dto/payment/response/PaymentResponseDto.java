package com.ontop.challenge.backend.apirest.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class PaymentResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 7726720948755010947L;

    @JsonProperty("requestInfo")
    private RequestInfoDto requestInfo;

    @JsonProperty("paymentInfo")
    private PaymentInfoDto paymentInfo;
}
