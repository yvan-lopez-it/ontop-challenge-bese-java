package com.ontop.challenge.backend.apirest.dto.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletTransactionResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6349977751519107543L;

    @JsonProperty("wallet_transaction_id")
    private Long walletTransactionId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;
}
