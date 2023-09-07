package com.ontop.challenge.backend.apirest.repositories;

import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionDao extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.recipient.id = :recipientId " +
        "AND (:amountSent is null OR t.amountSent = :amountSent) " +
        "AND (SUBSTRING(t.createdAt, 1, 10) = :createdAt OR :createdAt IS NULL) " +
        "ORDER BY t.createdAt DESC")
    Page<Transaction> findTransactionsByRecipientIdAndFilters(
        Long recipientId,
        Double amountSent,
        String createdAt,
        Pageable pageable
    );

    List<Transaction> findByStatus(Status status);
}
