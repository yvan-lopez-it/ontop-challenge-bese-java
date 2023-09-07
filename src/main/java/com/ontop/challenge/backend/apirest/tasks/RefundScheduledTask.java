package com.ontop.challenge.backend.apirest.tasks;

import com.ontop.challenge.backend.apirest.builders.TransactionBuilder;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import com.ontop.challenge.backend.apirest.services.IWalletService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RefundScheduledTask {

    private final Logger log = LoggerFactory.getLogger(RefundScheduledTask.class);

    private final ITransactionService transactionService;
    private final IWalletService walletService;

    private final String txMsgRefund;

    public RefundScheduledTask(ITransactionService transactionService, IWalletService walletService,
        @Value("${transaction.messages.refund}") String txMsgRefund) {
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.txMsgRefund = txMsgRefund;
    }

    @Scheduled(fixedDelay = 60 * 1000) // For the sake of the demo challenge, run every 1 minute (adjust as needed)
    public void processFailedWithdrawals() {
        List<Transaction> failedTransactions = transactionService.findByStatus(Status.FAILED_TO_REFUND);
        List<Transaction> refundedTransactions = new ArrayList<>(failedTransactions.size());
        List<Transaction> updatedTransactions = new ArrayList<>(failedTransactions.size());

        log.warn("[TASK] START - Refund failed transactions.");

        if (!failedTransactions.isEmpty()) {
            log.info("Proceed to refund for {} transactions", failedTransactions.size());

            failedTransactions.forEach(failedTransaction -> {
                // Update user wallet balance
                walletService.updateWallet(failedTransaction.getUserId(), failedTransaction.getRecipientGets(), false);

                // Set refund transaction and add to list
                Transaction refundedTransaction = this.createRefundTransaction(failedTransaction);
                refundedTransactions.add(refundedTransaction);

                // Transactions to be updated
                failedTransaction.setStatus(Status.FAILED);
                updatedTransactions.add(failedTransaction);
            });

            // Save refunded transactions
            transactionService.saveAllTransactions(refundedTransactions);

            // Update failed transactions status with FAILED instead of FAILED_TO_REFUND
            transactionService.saveAllTransactions(updatedTransactions);

        } else {
            log.info("No transactions to refund were found");
        }

        log.info("[TASK] COMPLETED - Refund failed transactions");
    }


    private Transaction createRefundTransaction(Transaction failedTransaction) {

        return TransactionBuilder.buildTransaction(
            failedTransaction.getUserId(),
            0.0,
            0.0,
            0.0,
            failedTransaction.getRecipient(),
            failedTransaction.getId(),
            failedTransaction.getAmountSent(),
            txMsgRefund,
            Status.REFUNDED
        );
    }

}
