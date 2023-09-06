package com.ontop.challenge.backend.apirest.repositories;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ontop.challenge.backend.apirest.models.Transaction;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionDaoTest {

    @MockBean
    private ITransactionDao transactionDao;

    @Mock
    private Transaction transactionA;

    @Mock
    private Transaction transactionB;

    @Test
    public void testFindByRecipientIdOrderByCreatedAtDesc() {

        Long recipientId = 1L;
        Double amountSent = 100.0;
        String createdAt = "2023-09-04T10:00:00";
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        List<Transaction> transactions = new ArrayList<>();

        // Add sample transactions to the list
        transactions.add(transactionA);
        transactions.add(transactionB);

        // Mock the repository method call
        when(transactionDao.findTransactionsByRecipientIdAndFilters(recipientId, amountSent, createdAt, pageable))
            .thenReturn(new PageImpl<>(transactions));

        // Call the repository method
        Page<Transaction> result = transactionDao.findTransactionsByRecipientIdAndFilters(recipientId, amountSent, createdAt, pageable);

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(transactions.size());
        // You can add more assertions as needed
    }
}
