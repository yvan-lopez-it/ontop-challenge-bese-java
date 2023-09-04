package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.models.Transaction;

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

}
