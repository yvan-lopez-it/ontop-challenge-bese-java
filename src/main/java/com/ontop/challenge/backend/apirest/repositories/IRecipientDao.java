package com.ontop.challenge.backend.apirest.repositories;

import com.ontop.challenge.backend.apirest.models.Recipient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRecipientDao extends CrudRepository<Recipient, Long> {

}
