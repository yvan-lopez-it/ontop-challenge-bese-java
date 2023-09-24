package com.ontop.challenge.backend.apirest.controllers;

import com.ontop.challenge.backend.apirest.dto.recipient.RecipientDto;
import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import com.ontop.challenge.backend.apirest.mapper.RecipientMapper;
import com.ontop.challenge.backend.apirest.services.IRecipientService;
import java.util.List;
import java.util.stream.Collectors;
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

    private final RecipientMapper recipientMapper;

    @Autowired
    public RecipientRestController(IRecipientService recipientService, RecipientMapper recipientMapper) {
        this.recipientService = recipientService;
        this.recipientMapper = recipientMapper;
    }

    @GetMapping
    public @NotNull ResponseEntity<List<RecipientDto>> getAllRecipients() {
        List<RecipientEntity> recipientEntities = recipientService.findAll();
        List<RecipientDto> recipientDtoList = recipientMapper.toListDto(recipientEntities);

        return new ResponseEntity<>(recipientDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public @NotNull ResponseEntity<RecipientDto> getRecipientById(@PathVariable Long id) {
        RecipientEntity recipientEntity = recipientService.findById(id);

        if (recipientEntity != null) {
            return new ResponseEntity<>(recipientMapper.toDto(recipientEntity), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public @NotNull ResponseEntity<RecipientDto> saveRecipient(@Validated @RequestBody RecipientDto recipientDto) {
        RecipientEntity recipientEntity = recipientMapper.toEntity(recipientDto);
        RecipientEntity savedRecipientEntity = recipientService.saveRecipient(recipientEntity);
        RecipientDto responseDto = recipientMapper.toDto(savedRecipientEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
