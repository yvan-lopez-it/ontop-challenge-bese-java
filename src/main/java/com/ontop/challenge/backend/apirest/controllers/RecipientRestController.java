package com.ontop.challenge.backend.apirest.controllers;

import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.services.IRecipientService;
import java.util.List;
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
    public ResponseEntity<List<Recipient>> getAllRecipients() {
        List<Recipient> recipients = recipientService.findAll();
        return new ResponseEntity<>(recipients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipient> getRecipientById(@PathVariable Long id) {
        Recipient recipient = recipientService.findById(id);

        if (recipient != null) {
            return new ResponseEntity<>(recipient, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Recipient> saveRecipient(@Validated @RequestBody Recipient recipient) {
        Recipient savedRecipient = recipientService.saveRecipient(recipient);
        return new ResponseEntity<>(savedRecipient, HttpStatus.CREATED);
    }
}
