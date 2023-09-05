package com.ontop.challenge.backend.apirest.services.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.repositories.IRecipientDao;
import com.ontop.challenge.backend.apirest.repositories.ITransactionDao;
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

@RunWith(MockitoJUnitRunner.class)
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

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
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGetTransactionsByRecipientId() {
        // Prepare test data
        Long recipientId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));

        List<Transaction> transactions = Arrays.asList(transactionA, transactionB);
        Page<Transaction> expectedPage = new PageImpl<>(transactions);

        // Mock the repository method
        when(transactionDao.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable)).thenReturn(expectedPage);

        // Call the service method
        Page<Transaction> result = transactionService.getTransactionsByRecipientId(recipientId, pageable);

        // Assert
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testPerformWalletToBankTransaction() {
        //TODO: testPerformWalletToBankTransaction() method
    }


    // Create a mock Transaction object for testing
    private Transaction createMockTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmountSent(100.0);
        transaction.setMessage("Bank transfer to your account");
        // Set other properties as needed for your test
        return transaction;
    }

    // Create a mock Page<Transaction> object for testing
    private Page<Transaction> createMockTransactionPage() {
        List<Transaction> transactions = Arrays.asList(createMockTransaction(), createMockTransaction());
        return new PageImpl<>(transactions);
    }


}
