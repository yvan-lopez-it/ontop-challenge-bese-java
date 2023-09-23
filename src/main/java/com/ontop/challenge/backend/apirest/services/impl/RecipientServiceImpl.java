package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import com.ontop.challenge.backend.apirest.exceptions.RecipientNotFoundException;
import com.ontop.challenge.backend.apirest.repositories.IRecipientDao;
import com.ontop.challenge.backend.apirest.services.IRecipientService;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipientServiceImpl implements IRecipientService {

    private final IRecipientDao recipientDao;

    @Autowired
    public RecipientServiceImpl(IRecipientDao recipientDao) {
        this.recipientDao = recipientDao;
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull List<RecipientEntity> findAll() {
        return (List<RecipientEntity>) recipientDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable RecipientEntity findById(@NotNull Long id) {
        return recipientDao.findById(id).orElse(null);
    }

    @Override
    public @NotNull RecipientEntity saveRecipient(@NotNull RecipientEntity recipientEntity) {
        return recipientDao.save(recipientEntity);
    }

    @Override
    public @NotNull RecipientEntity getRecipient(@NotNull Long recipientId) {
        RecipientEntity recipientEntity = recipientDao.findById(recipientId).orElse(null);
        this.ensureRecipientExists(recipientEntity);
        return recipientEntity;
    }

    private void ensureRecipientExists(@NotNull RecipientEntity recipientEntity) {
        if (recipientEntity == null) {
            throw new RecipientNotFoundException("RecipientEntity not found.");
        }
    }
}
