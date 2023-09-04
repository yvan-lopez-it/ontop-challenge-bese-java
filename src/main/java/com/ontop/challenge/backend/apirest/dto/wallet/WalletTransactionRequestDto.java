package com.ontop.challenge.backend.apirest.dto.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletTransactionRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 6479604666531996310L;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("user_id")
    private Long userId;

}
