package com.ontop.challenge.backend.apirest.builders;

import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;

public class TransactionBuilder {

    public static Transaction buildTransaction(
        Long userId,
        Double amountSent,
        Double transactionFee,
        Double recipientGets,
        Recipient recipient,
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
            .recipient(recipient)
            .associatedTransactionId(associatedTransactionId)
            .refundedAmount(refundedAmount)
            .message(message)
            .build();
    }

    public static Transaction buildTransaction(Long userId, Double amountSent, Double transactionFee, Double recipientGets, Recipient recipient,
        String message, Status transactionStatus) {
        return buildTransaction(
            userId, amountSent, transactionFee, recipientGets, recipient, null, 0.0, message, transactionStatus);
    }
}
