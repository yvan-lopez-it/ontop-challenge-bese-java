package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.models.Transaction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {

    /**
     * Performs the transaction between the bank accounts and updates the balance.
     *
     * @param userId
     * @param recipientId
     * @param amount
     * @return The transaction result.
     */
    Transaction performWalletToBankTransaction(Long userId, Long recipientId, Double amount);

    Page<Transaction> getTransactionsByRecipientId(Long recipientId, Pageable pageable);
}
