package com.ontop.challenge.backend.apirest.dto.transaction;

import com.ontop.challenge.backend.apirest.dto.recipient.RecipientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private Long id;
    private Double amountSent;
    private Double transactionFee;
    private Double recipientGets;
    private Double refundedAmount;
    private Long associatedTransactionId;
    private Long userId;
    private String createdAt;
    private String status;
    private String message;
    private RecipientDto recipient;

}
