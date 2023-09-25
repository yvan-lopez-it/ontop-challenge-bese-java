package com.ontop.challenge.backend.apirest.repositories;

import com.ontop.challenge.backend.apirest.entities.TransactionEntity;

import com.ontop.challenge.backend.apirest.enums.TransactionStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionDao extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.recipient.id = :recipientId " +
        "AND (:amountSent is null OR t.amountSent = :amountSent) " +
        "AND (t.createdAt = :createdAt OR :createdAt IS NULL) " +
        "ORDER BY t.createdAt DESC")
    Page<TransactionEntity> findTransactionsByRecipientId(
        Long recipientId,
        Double amountSent,
        LocalDateTime createdAt,
        Pageable pageable
    );

    List<TransactionEntity> findByStatus(TransactionStatus status);
}
