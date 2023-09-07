package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {

    List<Transaction> saveAllTransactions(List<Transaction> transactions);

    Transaction performWalletToBankTransaction(Long userId, Long recipientId, Double amount);

    Page<Transaction> getTransactionsByRecipientId(Long recipientId, Double amountSent, String createdAt, Pageable pageable);

    List<Transaction> findByStatus(Status status);
}
