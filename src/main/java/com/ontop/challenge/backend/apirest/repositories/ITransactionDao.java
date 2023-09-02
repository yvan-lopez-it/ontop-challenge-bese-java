package com.ontop.challenge.backend.apirest.repositories;

import com.ontop.challenge.backend.apirest.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITransactionDao extends JpaRepository<Transaction, Long> {

}
