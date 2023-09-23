package com.ontop.challenge.backend.apirest.builders;

import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import com.ontop.challenge.backend.apirest.entities.Transaction;
import com.ontop.challenge.backend.apirest.entities.Transaction.Status;

public class TransactionBuilder {

    public static Transaction buildTransaction(
        Long userId,
        Double amountSent,
        Double transactionFee,
        Double recipientGets,
        RecipientEntity recipientEntity,
        Long associatedTransactionId,
        Double refundedAmount,
        String message,
        Status transactionStatus) {
        return Transaction.builder()
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

    public static Transaction buildTransaction(Long userId, Double amountSent, Double transactionFee, Double recipientGets, RecipientEntity recipientEntity,
        String message, Status transactionStatus) {
        return buildTransaction(
            userId, amountSent, transactionFee, recipientGets, recipientEntity, null, 0.0, message, transactionStatus);
    }
}
