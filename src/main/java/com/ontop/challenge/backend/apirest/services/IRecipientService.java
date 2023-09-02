package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.models.Recipient;
import java.util.List;

public interface IRecipientService {

    List<Recipient> findAll();

    Recipient findById(Long id);

    Recipient saveRecipient(Recipient recipient);
}
