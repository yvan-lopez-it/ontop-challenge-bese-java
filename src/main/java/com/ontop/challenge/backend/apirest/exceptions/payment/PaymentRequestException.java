package com.ontop.challenge.backend.apirest.exceptions.payment;

import java.io.Serial;
import org.springframework.web.client.HttpStatusCodeException;

public class PaymentRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3325972334543702374L;

    public PaymentRequestException(String message) {
        super(message);
    }

    public PaymentRequestException(String message, HttpStatusCodeException ex) {
        super(message);
    }

}
