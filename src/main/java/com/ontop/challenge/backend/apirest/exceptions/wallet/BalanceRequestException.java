package com.ontop.challenge.backend.apirest.exceptions.wallet;

import java.io.Serial;
import org.springframework.web.client.HttpStatusCodeException;

public class BalanceRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3325972334543702374L;

    public BalanceRequestException(String message) {
        super(message);
    }

    public BalanceRequestException(String message, HttpStatusCodeException e) {
        super(message);
    }

}
