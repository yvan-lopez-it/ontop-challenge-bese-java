package com.ontop.challenge.backend.apirest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BalanceResponseDto {

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("user_id")
    private Long userId;

}
