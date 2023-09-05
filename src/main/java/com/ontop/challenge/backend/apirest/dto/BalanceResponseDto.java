package com.ontop.challenge.backend.apirest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponseDto {

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("user_id")
    private Long userId;

    public BalanceResponseDto(Double balance) {
    }
}
