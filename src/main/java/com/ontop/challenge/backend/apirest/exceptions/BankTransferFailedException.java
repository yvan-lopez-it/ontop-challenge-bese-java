package com.ontop.challenge.backend.apirest.exceptions;

import java.io.Serial;

public class BankTransferFailedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7391123623813727658L;

    public BankTransferFailedException(String message) {
        super(message);
    }

    public BankTransferFailedException(String message, Exception e) {
        super(message);
    }

}
