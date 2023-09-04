package com.ontop.challenge.backend.apirest.exceptions;

import java.io.Serial;

public class RecipientNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7250227763343997042L;

    public RecipientNotFoundException(String message) {
        super(message);
    }

}
