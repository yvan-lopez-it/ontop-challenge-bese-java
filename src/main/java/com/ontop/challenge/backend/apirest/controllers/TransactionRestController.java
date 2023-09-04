package com.ontop.challenge.backend.apirest.controllers;

import com.ontop.challenge.backend.apirest.dto.TransactionRequestDto;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        @RequestParam(defaultValue = "2") int size
    ) {
        // Create a Pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));

        // Call your service method to retrieve transactions by recipientId using pageable
        Page<Transaction> transactions = transactionService.getTransactionsByRecipientId(recipientId, pageable);

        // Return the transactions as a ResponseEntity
        return ResponseEntity.ok(transactions);

    }

    @PostMapping("/perform")
    public ResponseEntity<?> performTransaction(@RequestBody @Valid TransactionRequestDto request) {
        try {
            Transaction transaction = transactionService.performWalletToBankTransaction(request.getUserId(), request.getRecipientId(), request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (WalletInsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (BankTransferFailedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
