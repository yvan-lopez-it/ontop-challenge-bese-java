package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.entities.Transaction;

public interface IPaymentService {

    void performPayment(Transaction transaction);

}
