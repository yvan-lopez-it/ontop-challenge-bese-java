package com.ontop.challenge.backend.apirest.controllers;

import com.ontop.challenge.backend.apirest.dto.TransactionRequestDto;
import com.ontop.challenge.backend.apirest.entities.TransactionEntity;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.exceptions.RecipientNotFoundException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.BalanceRequestException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final ITransactionService transactionService;

    @Autowired
    public TransactionRestController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/perform")
    public @NotNull ResponseEntity<?> performTransaction(@RequestBody @Valid @NotNull TransactionRequestDto request, @NotNull BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                .stream()
                .map(e -> "Field '" + e.getField() + "' " + Objects.requireNonNull(e.getDefaultMessage()))
                .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            TransactionEntity transactionEntity = transactionService.performWalletToBankTransaction(request.getUserId(), request.getRecipientId(),
                request.getAmount());
            return ResponseEntity.ok(transactionEntity);
        } catch (RecipientNotFoundException e) {
            response.put("message", "RecipientEntity not found.");
            response.put("error", e.getMessage() + ": " + e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (BankTransferFailedException | WalletInsufficientBalanceException | BalanceRequestException e) {
            response.put("message", "There was an internal error.");
            response.put("error", e.getMessage() + ": " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/recipient/{recipientId}")
    public @NotNull ResponseEntity<?> getTransactionsByRecipientId(
        @PathVariable Long recipientId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "2") int size,
        @RequestParam(required = false) Double amountSent,
        @RequestParam(required = false) String createdAt
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        Page<TransactionEntity> transactions = transactionService
            .getTransactionsByRecipientId(recipientId, amountSent, createdAt, pageable);

        return ResponseEntity.ok(transactions);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @NotNull Map<String, String> handleValidationExceptions(@NotNull MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
