package com.ontop.challenge.backend.apirest.tasks;

import com.ontop.challenge.backend.apirest.builders.TransactionBuilder;
import com.ontop.challenge.backend.apirest.entities.TransactionEntity;

import com.ontop.challenge.backend.apirest.enums.TransactionStatus;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import com.ontop.challenge.backend.apirest.services.IWalletService;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
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

    @Scheduled(fixedDelay = 5 * 60 * 1000) // For the sake of the demo challenge, run every 5 minute (adjust as needed)
    public void processFailedWithdrawals() {
        List<TransactionEntity> failedTransactionEntities = transactionService.findByStatus(TransactionStatus.FAILED_TO_REFUND);
        List<TransactionEntity> refundedTransactionEntities = new ArrayList<>(failedTransactionEntities.size());
        List<TransactionEntity> updatedTransactionEntities = new ArrayList<>(failedTransactionEntities.size());

        log.warn("[TASK] START - Refunding 'failed' transactions.");

        if (!failedTransactionEntities.isEmpty()) {
            log.info("Proceed to refund for {} transactions", failedTransactionEntities.size());

            failedTransactionEntities.forEach(failedTransaction -> {
                // Update user wallet balance
                walletService.updateWallet(failedTransaction.getUserId(), failedTransaction.getRecipientGets(), false);

                // Create refund transaction and add to list
                TransactionEntity refundedTransactionEntity = this.createRefundTransaction(failedTransaction);
                refundedTransactionEntities.add(refundedTransactionEntity);

                // Transactions to be updated
                failedTransaction.setStatus(TransactionStatus.FAILED);
                updatedTransactionEntities.add(failedTransaction);
            });

            // Save refunded transactions
            transactionService.saveAllTransactions(refundedTransactionEntities);

            // Update failed transactions status with FAILED instead of FAILED_TO_REFUND
            transactionService.saveAllTransactions(updatedTransactionEntities);

        } else {
            log.info("No transactions to refund were found");
        }

        log.info("[TASK] COMPLETED - Refunding 'failed' transactions");
    }


    private TransactionEntity createRefundTransaction(@NotNull TransactionEntity failedTransactionEntity) {

        return TransactionBuilder.buildTransaction(
            failedTransactionEntity.getUserId(),
            0.0,
            0.0,
            0.0,
            failedTransactionEntity.getRecipient(),
            failedTransactionEntity.getId(),
            failedTransactionEntity.getAmountSent(),
            txMsgRefund,
            TransactionStatus.REFUNDED
        );
    }

}
