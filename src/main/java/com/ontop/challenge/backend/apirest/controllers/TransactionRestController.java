package com.ontop.challenge.backend.apirest.controllers;

import com.ontop.challenge.backend.apirest.dto.TransactionRequestDto;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.exceptions.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<?> performTransaction(@RequestBody TransactionRequestDto request) {
        try {
            Transaction transaction = transactionService.performWalletToBankTransaction(request.getRecipientId(), request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (WalletInsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (BankTransferFailedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
