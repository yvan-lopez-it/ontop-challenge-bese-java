package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.models.Transaction;

public interface ITransactionService {

    Transaction performWalletToBankTransaction(Long id, Double amount);

}
