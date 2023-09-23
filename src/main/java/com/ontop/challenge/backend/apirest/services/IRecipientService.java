package com.ontop.challenge.backend.apirest.services;

import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface IRecipientService {

    List<RecipientEntity> findAll();

    @Nullable RecipientEntity findById(Long id);

    RecipientEntity saveRecipient(RecipientEntity recipientEntity);

    RecipientEntity getRecipient(Long recipientId);
}
