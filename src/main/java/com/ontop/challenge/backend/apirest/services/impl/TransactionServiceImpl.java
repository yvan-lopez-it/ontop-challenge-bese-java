package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.dto.BalanceResponseDto;
import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;
import com.ontop.challenge.backend.apirest.repositories.IRecipientDao;
import com.ontop.challenge.backend.apirest.repositories.ITransactionDao;
import com.ontop.challenge.backend.apirest.services.ITransactionService;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private static final String MOCK_USER_ID = "1";

    @Value("${wallet.balance.url}")
    private String walletBalanceUrl;

    private final IRecipientDao recipientDao;
    private final ITransactionDao transactionDao;
    private final RestTemplate restTemplate;

    @Autowired
    public TransactionServiceImpl(IRecipientDao recipientDao, ITransactionDao transactionDao, RestTemplate restTemplate) {
        this.recipientDao = recipientDao;
        this.transactionDao = transactionDao;
        this.restTemplate = restTemplate;
    }

    @Override
    public Transaction performWalletToBankTransaction(Long recipientId, Double amount) {

        //Get Balance
        Double userBalance = this.getBalance();
        System.out.println("userBalance: " + userBalance);

        //Get Recipient
        Recipient recipient = this.getRecipient(recipientId);

        //TODO:
        if (recipient == null) {
            //throw exception recipient not found.
        }

        //Set transaction
        Transaction tx = new Transaction();
        tx.setAvailableWalletFunds(userBalance);
        tx.setAmountSent(amount);
        tx.setTransactionFee(tx.getAmountSent());
        tx.setRecipientGets(tx.getAmountSent(), tx.getTransactionFee());
        tx.setStatus(Status.IN_PROGRESS);
        tx.setRecipient(recipient);

        Transaction savedTx = transactionDao.save(tx);

        return savedTx;
    }

    private Recipient getRecipient(Long recipientId) {
        return recipientDao.findById(recipientId).orElse(null);
    }

    private Double getBalance() {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(walletBalanceUrl)
            .append("?")
            .append("user_id")
            .append("=")
            .append(MOCK_USER_ID);

        ResponseEntity<BalanceResponseDto> response = restTemplate.getForEntity(sbUrl.toString(), BalanceResponseDto.class);
        return Objects.requireNonNull(response.getBody()).getBalance();
    }

}
