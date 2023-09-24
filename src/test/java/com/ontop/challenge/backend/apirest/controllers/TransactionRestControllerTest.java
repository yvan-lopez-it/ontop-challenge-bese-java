package com.ontop.challenge.backend.apirest.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.ontop.challenge.backend.apirest.dto.TransactionRequestDto;
import com.ontop.challenge.backend.apirest.entities.TransactionEntity;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@RunWith(MockitoJUnitRunner.class)
public class TransactionRestControllerTest {

    @InjectMocks
    private TransactionRestController transactionController;

    @Mock
    private ITransactionService transactionService;

    @Mock
    private TransactionEntity transactionEntityA;

    @Mock
    private TransactionEntity transactionEntityB;

    @Mock
    private TransactionRequestDto transactionRequestDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTransactionsByRecipientId() {
        // Prepare test data
        Long recipientId = 1L;
        Double amountSent = 100.0;
        String createdAt = "2023-09-04T10:00:00";
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt"));
        List<TransactionEntity> transactionEntities = Arrays.asList(transactionEntityA, transactionEntityB);
        Page<TransactionEntity> page = new PageImpl<>(transactionEntities);

        // Mock the service method
        when(transactionService.getTransactionsByRecipientId(recipientId, amountSent, createdAt, pageable))
            .thenReturn(page);

        // Perform the GET request to the controller
        ResponseEntity<?> response = transactionController
            .getTransactionsByRecipientId(recipientId, 0, 2, amountSent, createdAt);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Page);
    }

    @Test
    public void testPerformTransaction() {

        // Mock the service method
        when(transactionService.performWalletToBankTransaction(
            transactionRequestDto.getUserId(),
            transactionRequestDto.getRecipientId(),
            transactionRequestDto.getAmount())
        ).thenReturn(transactionEntityA);

        // Prepare a mock BindingResult with validation errors
        BindingResult mockBindingResult = Mockito.mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        // Perform the POST request to the controller
        ResponseEntity<?> response = transactionController.performTransaction(transactionRequestDto, mockBindingResult);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof TransactionEntity);
    }

    @Test
    public void testPerformTransaction_InsufficientBalanceException() {

        // Mock the service method to throw WalletInsufficientBalanceException
        when(transactionService.performWalletToBankTransaction(
            transactionRequestDto.getUserId(),
            transactionRequestDto.getRecipientId(),
            transactionRequestDto.getAmount())
        ).thenThrow(new WalletInsufficientBalanceException("Insufficient Balance"));

        // Prepare a mock BindingResult with validation errors
        BindingResult mockBindingResult = Mockito.mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        // Perform the POST request to the controller
        ResponseEntity<?> response = transactionController.performTransaction(transactionRequestDto, mockBindingResult);

        // Assert the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testPerformTransaction_BankTransferFailedException() {
        // Mock the service method to throw BankTransferFailedException
        when(transactionService.performWalletToBankTransaction(
            transactionRequestDto.getUserId(),
            transactionRequestDto.getRecipientId(),
            transactionRequestDto.getAmount())
        ).thenThrow(new BankTransferFailedException("Bank transfer failed"));

        // Prepare a mock BindingResult with validation errors
        BindingResult mockBindingResult = Mockito.mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        // Perform the POST request to the controller
        ResponseEntity<?> response = transactionController.performTransaction(transactionRequestDto, mockBindingResult);

        // Assert the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

}
