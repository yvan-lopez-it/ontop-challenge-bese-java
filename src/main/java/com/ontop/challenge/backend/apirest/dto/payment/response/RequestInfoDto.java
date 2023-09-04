package com.ontop.challenge.backend.apirest.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class RequestInfoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 485599491578237919L;

    @JsonProperty("status")
    private String status;


}
