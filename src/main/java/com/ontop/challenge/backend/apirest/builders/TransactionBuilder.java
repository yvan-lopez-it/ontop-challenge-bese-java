package com.ontop.challenge.backend.apirest.builders;

import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import com.ontop.challenge.backend.apirest.entities.TransactionEntity;
import com.ontop.challenge.backend.apirest.enums.TransactionStatus;


public class TransactionBuilder {

    public static TransactionEntity buildTransaction(
        Long userId,
        Double amountSent,
        Double transactionFee,
        Double recipientGets,
        RecipientEntity recipientEntity,
        Long associatedTransactionId,
        Double refundedAmount,
        String message,
        TransactionStatus transactionStatus) {
        return TransactionEntity.builder()
            .transactionFee(transactionFee)
            .amountSent(amountSent)
            .recipientGets(recipientGets)
            .userId(userId)
            .status(transactionStatus)
            .recipient(recipientEntity)
            .associatedTransactionId(associatedTransactionId)
            .refundedAmount(refundedAmount)
            .message(message)
            .build();
    }

    public static TransactionEntity buildTransaction(Long userId, Double amountSent, Double transactionFee, Double recipientGets, RecipientEntity recipientEntity,
        String message, TransactionStatus transactionStatus) {
        return buildTransaction(
            userId, amountSent, transactionFee, recipientGets, recipientEntity, null, 0.0, message, transactionStatus);
    }
}
