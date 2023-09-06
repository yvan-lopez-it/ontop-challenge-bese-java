package com.ontop.challenge.backend.apirest.controllers;

import com.ontop.challenge.backend.apirest.dto.TransactionRequestDto;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.BalanceRequestException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final ITransactionService transactionService;

    @Autowired
    public TransactionRestController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<?> getTransactionsByRecipientId(
        @PathVariable Long recipientId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        Page<Transaction> transactions = transactionService.getTransactionsByRecipientId(recipientId, pageable);

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/perform")
    public ResponseEntity<?> performTransaction(@RequestBody @Valid TransactionRequestDto request, BindingResult result) {

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
            Transaction transaction = transactionService.performWalletToBankTransaction(request.getUserId(), request.getRecipientId(), request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (BankTransferFailedException | BalanceRequestException e) {
            response.put("mesage", "There was an internal error.");
            response.put("error", e.getMessage() + ": " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (WalletInsufficientBalanceException e) {
            response.put("mesage", "There was bad request error.");
            response.put("error", e.getMessage() + ": " + e.getCause());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
