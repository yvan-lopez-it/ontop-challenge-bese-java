package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {

    Transaction performWalletToBankTransaction(Long userId, Long recipientId, Double amount);

    Page<Transaction> getTransactionsByRecipientId(Long recipientId, Pageable pageable);
}
