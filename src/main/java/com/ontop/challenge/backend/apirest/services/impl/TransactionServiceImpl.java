package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.exceptions.RecipientNotFoundException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;
import com.ontop.challenge.backend.apirest.repositories.IRecipientDao;
import com.ontop.challenge.backend.apirest.repositories.ITransactionDao;
import com.ontop.challenge.backend.apirest.services.IPaymentService;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import com.ontop.challenge.backend.apirest.services.IWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final IRecipientDao recipientDao;
    private final ITransactionDao transactionDao;
    private final IWalletService walletService;
    private final IPaymentService paymentService;

    private final Double feePercentageVal;

    @Value("${transaction.messages.default}")
    private String txMsgDefault;

    @Value("${transaction.messages.refund}")
    private String txMsgRefund;


    @Autowired
    public TransactionServiceImpl(IWalletService walletService, IRecipientDao recipientDao, ITransactionDao transactionDao,
        IPaymentService paymentService, @Value("${fee.percentage.val}") Double feePercentageVal) {
        this.walletService = walletService;
        this.recipientDao = recipientDao;
        this.transactionDao = transactionDao;
        this.paymentService = paymentService;
        this.feePercentageVal = feePercentageVal;
    }

    @Override
    public Transaction performWalletToBankTransaction(Long userId, Long recipientId, Double amountSent) {

        // Check user balance
        Double userBalance = walletService.getBalance(userId);
        this.ensureSufficientBalance(userBalance, amountSent);

        // Retrieve recipient
        Recipient recipient = getRecipient(recipientId);
        this.ensureRecipientExists(recipient);

        // Calculate transaction fee and recipient gets
        Double transactionFee = this.calculateTransactionFee(amountSent);
        Double recipientGets = this.calculateRecipientGets(amountSent, transactionFee);

        // Update wallet using WalletService
        log.info("Perform update wallet call");
        walletService.updateWallet(userId, recipientGets, true);

        // Create and save transaction
        Transaction savedTransaction = createAndSaveTransaction(amountSent, transactionFee, recipientGets, recipient);

        // Perform payment
        paymentService.performPayment(savedTransaction);

        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsByRecipientId(Long recipientId, Pageable pageable) {
        return transactionDao.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    private void ensureSufficientBalance(Double userBalance, Double amountSent) {
        if (userBalance < amountSent) {
            throw new WalletInsufficientBalanceException("Insufficient balance amount.");
        }
    }

    private void ensureRecipientExists(Recipient recipient) {
        if (recipient == null) {
            throw new RecipientNotFoundException("Recipient not found.");
        }
    }

    private Double calculateTransactionFee(Double amountSent) {
        return amountSent * (feePercentageVal != null ? feePercentageVal : 0.10);
    }

    private Double calculateRecipientGets(Double amountSent, Double transactionFee) {
        return amountSent - transactionFee;
    }

    private Transaction createAndSaveTransaction(Double amountSent, Double transactionFee, Double recipientGets, Recipient recipient) {
        Status transactionStatus = Status.IN_PROGRESS;
        Transaction transaction = this.buildTransaction(amountSent, transactionFee, recipientGets, recipient, txMsgDefault, transactionStatus);
        return transactionDao.save(transaction);
    }

    private Transaction buildTransaction(Double amountSent, Double transactionFee, Double recipientGets, Recipient recipient,
        String message, Status transactionStatus) {

        return Transaction.builder()
            .transactionFee(transactionFee)
            .amountSent(amountSent)
            .recipientGets(recipientGets)
            .status(transactionStatus)
            .recipient(recipient)
            .message(message)
            .build();
    }

    private Recipient getRecipient(Long recipientId) {
        return recipientDao.findById(recipientId).orElse(null);
    }


}
