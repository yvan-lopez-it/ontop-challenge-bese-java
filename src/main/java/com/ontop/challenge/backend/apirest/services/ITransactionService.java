package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.entities.TransactionEntity;
import com.ontop.challenge.backend.apirest.enums.TransactionStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {

    List<TransactionEntity> saveAllTransactions(List<TransactionEntity> transactionEntities);

    TransactionEntity performWalletToBankTransaction(Long userId, Long recipientId, Double amount);

    Page<TransactionEntity> getTransactionsByRecipientId(Long recipientId, Double amountSent, String createdAt, Pageable pageable);

    List<TransactionEntity> findByStatus(TransactionStatus status);
}
