package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.entities.TransactionEntity;

public interface IPaymentService {

    void performPayment(TransactionEntity transactionEntity);

}
