package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.exceptions.RecipientNotFoundException;
import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.repositories.IRecipientDao;
import com.ontop.challenge.backend.apirest.services.IRecipientService;
import java.util.List;
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
    public List<Recipient> findAll() {
        return (List<Recipient>) recipientDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Recipient findById(Long id) {
        return recipientDao.findById(id).orElse(null);
    }

    @Override
    public Recipient saveRecipient(Recipient recipient) {
        return recipientDao.save(recipient);
    }

    @Override
    public Recipient getRecipient(Long recipientId) {
        Recipient recipient = recipientDao.findById(recipientId).orElse(null);
        this.ensureRecipientExists(recipient);
        return recipient;
    }

    private void ensureRecipientExists(Recipient recipient) {
        if (recipient == null) {
            throw new RecipientNotFoundException("Recipient not found.");
        }
    }
}
