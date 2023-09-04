package com.ontop.challenge.backend.apirest.repositories;

import com.ontop.challenge.backend.apirest.models.Transaction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionDao extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);
}
