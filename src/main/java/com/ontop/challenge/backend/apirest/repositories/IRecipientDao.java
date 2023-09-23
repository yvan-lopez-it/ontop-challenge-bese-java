package com.ontop.challenge.backend.apirest.repositories;

import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRecipientDao extends CrudRepository<RecipientEntity, Long> {

}
