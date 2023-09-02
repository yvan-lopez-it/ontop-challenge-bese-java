package com.ontop.challenge.backend.apirest.repositories;

import com.ontop.challenge.backend.apirest.models.Recipient;
import org.springframework.data.repository.CrudRepository;

public interface IRecipientDao extends CrudRepository<Recipient, Long> {

}
