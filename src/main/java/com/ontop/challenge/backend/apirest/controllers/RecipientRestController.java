package com.ontop.challenge.backend.apirest.controllers;

import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import com.ontop.challenge.backend.apirest.services.IRecipientService;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipients")
public class RecipientRestController {

    private final IRecipientService recipientService;

    @Autowired
    public RecipientRestController(IRecipientService recipientService) {
        this.recipientService = recipientService;
    }


    @GetMapping
    public @NotNull ResponseEntity<List<RecipientEntity>> getAllRecipients() {
        List<RecipientEntity> recipientEntities = recipientService.findAll();
        return new ResponseEntity<>(recipientEntities, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public @NotNull ResponseEntity<RecipientEntity> getRecipientById(@PathVariable Long id) {
        RecipientEntity recipientEntity = recipientService.findById(id);

        if (recipientEntity != null) {
            return new ResponseEntity<>(recipientEntity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public @NotNull ResponseEntity<RecipientEntity> saveRecipient(@Validated @RequestBody RecipientEntity recipientEntity) {
        RecipientEntity savedRecipientEntity = recipientService.saveRecipient(recipientEntity);
        return new ResponseEntity<>(savedRecipientEntity, HttpStatus.CREATED);
    }
}
