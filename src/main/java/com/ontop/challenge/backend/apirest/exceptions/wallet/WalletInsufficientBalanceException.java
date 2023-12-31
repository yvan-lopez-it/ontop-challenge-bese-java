package com.ontop.challenge.backend.apirest.exceptions.wallet;

import java.io.Serial;

public class WalletInsufficientBalanceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3325972334543702374L;

    public WalletInsufficientBalanceException(String message) {
        super(message);
    }

}
