package com.ontop.challenge.backend.apirest.services.impl;

import static java.util.Optional.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ontop.challenge.backend.apirest.dto.BalanceResponseDto;
import com.ontop.challenge.backend.apirest.dto.payment.response.PaymentResponseDto;
import com.ontop.challenge.backend.apirest.dto.wallet.WalletTransactionResponseDto;
import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.repositories.IRecipientDao;
import com.ontop.challenge.backend.apirest.repositories.ITransactionDao;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ITransactionDao transactionDao;

    @Mock
    private IRecipientDao recipientDao;

    @Mock
    private Transaction transactionA;

    @Mock
    private Transaction transactionB;

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
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));

        List<Transaction> transactions = Arrays.asList(transactionA, transactionB);
        Page<Transaction> expectedPage = new PageImpl<>(transactions);

        // Mock the repository method
        when(transactionDao.findTransactionsByRecipientIdAndFilters(recipientId, amountSent, createdAt, pageable))
            .thenReturn(expectedPage);

        // Call the service method
        Page<Transaction> result = transactionService.getTransactionsByRecipientId(recipientId, amountSent, createdAt, pageable);

        // Assert
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testPerformWalletToBankTransaction() {

        Long userId = 1L;
        Long recipientId = 1L;
        Double amountSent = 10.0;

        // Mocking getBalance
        when(restTemplate.getForEntity(anyString(), eq(BalanceResponseDto.class)))
            .thenReturn(new ResponseEntity<>(new BalanceResponseDto(15.0, userId), HttpStatus.OK));

        // Mocking getRecipient
        Recipient mockRecipient = new Recipient();
        when(recipientDao.findById(recipientId))
            .thenReturn(of(mockRecipient));

        // Mocking createWalletTransaction
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(WalletTransactionResponseDto.class)))
            .thenReturn(new ResponseEntity<>(new WalletTransactionResponseDto(), HttpStatus.OK));

        // Mocking transactionDao.save
        Transaction savedTransaction = new Transaction();
        when(transactionDao.save(any(Transaction.class)))
            .thenReturn(savedTransaction);

        // Mocking performPayment
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(PaymentResponseDto.class)))
            .thenReturn(new ResponseEntity<>(new PaymentResponseDto(), HttpStatus.OK));

        // Act
        ITransactionService transactionServiceMock = mock(ITransactionService.class);
        transactionServiceMock.performWalletToBankTransaction(userId, recipientId, amountSent);

        // Assert
        verify(transactionServiceMock, atLeastOnce()).performWalletToBankTransaction(userId, recipientId, amountSent);

    }

}
