package com.ontop.challenge.backend.apirest.repositories;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ontop.challenge.backend.apirest.entities.TransactionEntity;
import java.time.LocalDateTime;
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
    private TransactionEntity transactionEntityA;

    @Mock
    private TransactionEntity transactionEntityB;

    @Test
    public void testFindTransactionsByRecipientIdAndFilters() {

        Long recipientId = 1L;
        Double amountSent = 1000.0;
        String createdAt = "2023-09-04 00:00:00";
        LocalDateTime ldtCreated_at = LocalDateTime.parse(createdAt);

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        List<TransactionEntity> transactionEntities = new ArrayList<>();

        // Add sample transactionEntities to the list
        transactionEntities.add(transactionEntityA);
        transactionEntities.add(transactionEntityB);

        // Mock the repository method call
        when(transactionDao.findTransactionsByRecipientId(recipientId, amountSent, ldtCreated_at, pageable))
            .thenReturn(new PageImpl<>(transactionEntities));

        // Call the repository method
        Page<TransactionEntity> result = transactionDao.findTransactionsByRecipientId(recipientId, amountSent, ldtCreated_at, pageable);

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(transactionEntities.size());

    }
}
