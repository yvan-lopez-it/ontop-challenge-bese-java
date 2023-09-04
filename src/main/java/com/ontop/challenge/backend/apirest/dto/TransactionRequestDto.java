package com.ontop.challenge.backend.apirest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1106677210328490066L;

    private Long userId;
    private Long recipientId;
    private Double amount;
}
