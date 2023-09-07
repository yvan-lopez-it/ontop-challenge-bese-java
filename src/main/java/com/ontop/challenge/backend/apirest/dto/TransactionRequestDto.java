package com.ontop.challenge.backend.apirest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1106677210328490066L;

    @NotNull(message = "The userId is required")
    @Positive(message = "The amount should be greater than zero")
    private Long userId;

    @NotNull(message = "The recipientId is required")
    @Positive(message = "The amount should be greater than zero")
    private Long recipientId;

    @NotNull(message = "The amount is required")
    @Positive(message = "The amount should be greater than zero")
    private Double amount;
}
