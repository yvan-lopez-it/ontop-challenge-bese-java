package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.builders.TransactionBuilder;
import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.exceptions.payment.PaymentRequestException;
import com.ontop.challenge.backend.apirest.entities.Transaction;
import com.ontop.challenge.backend.apirest.entities.Transaction.Status;
import com.ontop.challenge.backend.apirest.repositories.ITransactionDao;
import com.ontop.challenge.backend.apirest.services.IPaymentService;
import com.ontop.challenge.backend.apirest.services.IRecipientService;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import com.ontop.challenge.backend.apirest.services.IWalletService;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final IRecipientService recipientService;
    private final ITransactionDao transactionDao;
    private final IWalletService walletService;
    private final IPaymentService paymentService;

    private final Double feePercentageVal;

    @Value("${transaction.messages.default}")
    private String txMsgDefault;

    @Autowired
    public TransactionServiceImpl(IWalletService walletService, IRecipientService recipientService, ITransactionDao transactionDao,
        IPaymentService paymentService, @Value("${fee.percentage.val}") Double feePercentageVal) {
        this.walletService = walletService;
        this.recipientService = recipientService;
        this.transactionDao = transactionDao;
        this.paymentService = paymentService;
        this.feePercentageVal = feePercentageVal;
    }

    @Override
    public @NotNull List<Transaction> saveAllTransactions(@NotNull List<Transaction> transactions) {
        return transactionDao.saveAll(transactions);
    }

    @Override
    public @NotNull Transaction performWalletToBankTransaction(Long userId, Long recipientId, Double amountSent) {

        // Check user balance
        walletService.checkBalance(userId, amountSent);

        // Retrieve recipientEntity
        RecipientEntity recipientEntity = recipientService.getRecipient(recipientId);

        // Calculate transaction fee and recipientEntity gets
        Double transactionFee = this.calculateTransactionFee(amountSent);
        Double recipientGets = this.calculateRecipientGets(amountSent, transactionFee);

        // Update wallet balance
        log.info("Perform update wallet call");
        walletService.updateWallet(userId, recipientGets, true);

        // Create and save transaction
        Transaction savedTransaction = this.createAndSaveTransaction(userId, amountSent, transactionFee, recipientGets, recipientEntity);

        // Perform payment
        try {
            paymentService.performPayment(savedTransaction);
        } catch (PaymentRequestException e) {
            log.error("Error performing payment: " + e.getMessage());
            this.handlePaymentException(savedTransaction, e);
        }

        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsByRecipientId(Long recipientId, Double amountSent, String createdAt, Pageable pageable) {
        return transactionDao.findTransactionsByRecipientIdAndFilters(recipientId, amountSent, createdAt, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByStatus(Status status) {
        return transactionDao.findByStatus(status);
    }

    private @NotNull Double calculateTransactionFee(Double amountSent) {
        return amountSent * (feePercentageVal != null ? feePercentageVal : 0.10);
    }

    private @NotNull Double calculateRecipientGets(Double amountSent, Double transactionFee) {
        return amountSent - transactionFee;
    }

    private @NotNull Transaction createAndSaveTransaction(Long userId, Double amountSent, Double transactionFee, Double recipientGets, RecipientEntity recipientEntity) {
        Status transactionStatus = Status.IN_PROGRESS;
        Transaction transaction =
            TransactionBuilder
                .buildTransaction(userId, amountSent, transactionFee, recipientGets, recipientEntity, txMsgDefault, transactionStatus);
        return transactionDao.save(transaction);
    }

    private void handlePaymentException(@NotNull Transaction transaction, @NotNull Exception e) {
        log.info("Transaction with status FAILED_TO_REFUND");
        transaction.setStatus(Status.FAILED_TO_REFUND);
        transactionDao.save(transaction);

        throw new BankTransferFailedException("Error performing payment: " + e.getMessage(), e);
    }
}
