package com.ontop.challenge.backend.apirest.builders;

import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;

public class TransactionBuilder {

    public static Transaction buildTransaction(Long userId, Double amountSent, Double transactionFee, Double recipientGets, Recipient recipient,
        String message, Status transactionStatus) {
        return Transaction.builder()
            .transactionFee(transactionFee)
            .amountSent(amountSent)
            .recipientGets(recipientGets)
            .userId(userId)
            .status(transactionStatus)
            .recipient(recipient)
            .message(message)
            .build();
    }

}
