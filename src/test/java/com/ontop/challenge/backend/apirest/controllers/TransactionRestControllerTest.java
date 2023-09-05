package com.ontop.challenge.backend.apirest.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.ontop.challenge.backend.apirest.dto.TransactionRequestDto;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class TransactionRestControllerTest {

    @InjectMocks
    private TransactionRestController transactionController;

    @Mock
    private ITransactionService transactionService;

    @Test
    public void testGetTransactionsByRecipientId() {
        // Prepare test data
        Long recipientId = 1L;
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt"));
        List<Transaction> transactions = Arrays.asList(
            new Transaction(/* initialize with data */),
            new Transaction(/* initialize with data */)
        );
        Page<Transaction> page = new PageImpl<>(transactions);

        // Mock the service method
        when(transactionService.getTransactionsByRecipientId(recipientId, pageable)).thenReturn(page);

        // Perform the GET request to the controller
        ResponseEntity<?> response = transactionController.getTransactionsByRecipientId(recipientId, 0, 2);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Page);

        // You can further assert the content of the page if needed
    }

    @Test
    public void testPerformTransaction() {
        // Prepare test data
        TransactionRequestDto request = new TransactionRequestDto(/* initialize with data */);
        Transaction transaction = new Transaction(/* initialize with data */);

        // Mock the service method
        when(transactionService.performWalletToBankTransaction(request.getUserId(), request.getRecipientId(), request.getAmount())).thenReturn(transaction);

        // Perform the POST request to the controller
        ResponseEntity<?> response = transactionController.performTransaction(request);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Transaction);

        // You can further assert the content of the transaction if needed
    }

    @Test
    public void testPerformTransaction_InsufficientBalanceException() {
        // Prepare test data
        TransactionRequestDto request = new TransactionRequestDto(/* initialize with data */);

        // Mock the service method to throw WalletInsufficientBalanceException
        when(transactionService.performWalletToBankTransaction(request.getUserId(), request.getRecipientId(), request.getAmount()))
            .thenThrow(new WalletInsufficientBalanceException("Insufficient Balance"));

        // Perform the POST request to the controller
        ResponseEntity<?> response = transactionController.performTransaction(request);

        // Assert the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testPerformTransaction_BankTransferFailedException() {
        // Prepare test data
        TransactionRequestDto request = new TransactionRequestDto(/* initialize with data */);

        // Mock the service method to throw BankTransferFailedException
        when(transactionService.performWalletToBankTransaction(request.getUserId(), request.getRecipientId(), request.getAmount()))
            .thenThrow(new BankTransferFailedException("Bank transfer failed"));

        // Perform the POST request to the controller
        ResponseEntity<?> response = transactionController.performTransaction(request);

        // Assert the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

}
