package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.builders.TransactionBuilder;
import com.ontop.challenge.backend.apirest.exceptions.BankTransferFailedException;
import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;
import com.ontop.challenge.backend.apirest.repositories.ITransactionDao;
import com.ontop.challenge.backend.apirest.services.IPaymentService;
import com.ontop.challenge.backend.apirest.services.IRecipientService;
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

    @Value("${transaction.messages.refund}")
    private String txMsgRefund;


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
    public Transaction performWalletToBankTransaction(Long userId, Long recipientId, Double amountSent) {

        // Check user balance
        walletService.checkBalance(userId, amountSent);

        // Retrieve recipient
        Recipient recipient = recipientService.getRecipient(recipientId);

        // Calculate transaction fee and recipient gets
        Double transactionFee = this.calculateTransactionFee(amountSent);
        Double recipientGets = this.calculateRecipientGets(amountSent, transactionFee);

        // Update wallet
        log.info("Perform update wallet call");
        walletService.updateWallet(userId, recipientGets, true);

        // Create and save transaction
        Transaction savedTransaction = this.createAndSaveTransaction(userId, amountSent, transactionFee, recipientGets, recipient);

        // Perform payment
        try {
            paymentService.performPayment(savedTransaction);
        } catch (BankTransferFailedException e) {
            handlePaymentException(savedTransaction, e);
        }

        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsByRecipientId(Long recipientId, Double amountSent, String createdAt, Pageable pageable) {
        return transactionDao.findTransactionsByRecipientIdAndFilters(recipientId, amountSent, createdAt, pageable);
    }

    private Double calculateTransactionFee(Double amountSent) {
        return amountSent * (feePercentageVal != null ? feePercentageVal : 0.10);
    }

    private Double calculateRecipientGets(Double amountSent, Double transactionFee) {
        return amountSent - transactionFee;
    }

    private Transaction createAndSaveTransaction(Long userId, Double amountSent, Double transactionFee, Double recipientGets, Recipient recipient) {
        Status transactionStatus = Status.IN_PROGRESS;
        Transaction transaction =
            TransactionBuilder.buildTransaction(userId, amountSent, transactionFee, recipientGets, recipient, txMsgDefault, transactionStatus);
        return transactionDao.save(transaction);
    }

    private void handlePaymentException(Transaction transaction, Exception ex) {
        log.error("Error performing payment: " + ex.getMessage());

        log.info("Transaction with status FAILED");
        transaction.setStatus(Status.FAILED);
        transactionDao.save(transaction);

        // TODO: Place this logic in a schedule config.
        // TODO: add to Transaction entity a Withdraw and TopUp wallet transaction status. Later we can filter the txs to refund.
        // Proceed with refund
        log.info("Proceed to refund");
        walletService.updateWallet(transaction.getUserId(), transaction.getRecipientGets(), false);
        transaction.setStatus(Status.REFUNDED);
        transaction.setMessage(txMsgRefund);
        transactionDao.save(transaction);

        throw new BankTransferFailedException("Error performing payment: " + ex.getMessage(), ex);
    }
}
