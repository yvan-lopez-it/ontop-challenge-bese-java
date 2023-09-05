package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.dto.BalanceResponseDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.AccountDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.DestinationDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.PaymentRequestDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.SourceDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.SourceInformationDto;
import com.ontop.challenge.backend.apirest.dto.payment.response.PaymentResponseDto;
import com.ontop.challenge.backend.apirest.dto.wallet.WalletTransactionRequestDto;
import com.ontop.challenge.backend.apirest.dto.wallet.WalletTransactionResponseDto;
import com.ontop.challenge.backend.apirest.exceptions.RecipientNotFoundException;
import com.ontop.challenge.backend.apirest.exceptions.payment.PaymentRequestException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletrRequestException;
import com.ontop.challenge.backend.apirest.models.Recipient;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.models.Transaction.Status;
import com.ontop.challenge.backend.apirest.repositories.IRecipientDao;
import com.ontop.challenge.backend.apirest.repositories.ITransactionDao;
import com.ontop.challenge.backend.apirest.services.ITransactionService;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Value("${wallet.balance.url}")
    private String walletBalanceUrl;

    @Value("${payment.external.api.url}")
    private String paymentExternalApiUrl;

    @Value("${wallet.transaction.url}")
    private String walletTransactionsUrl;

    @Value("${fee.percentage.val}")
    private Double feePercentageVal;

    @Value("${transaction.messages.default}")
    private String txMsgDefault;

    @Value("${transaction.messages.refund}")
    private String txMsgRefund;

    @Value("${ontop.source.info.name}")
    private String sourceInfoName;

    @Value("${ontop.account.number}")
    private String companyAccountNUmber;

    @Value("${currency.default}")
    private String defaultCurrency;

    @Value("${ontop.routing.number}")
    private String companyRoutingNumber;

    @Value("${ontop.source.type}")
    private String companySourceType;

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
    public Transaction performWalletToBankTransaction(Long userId, Long recipientId, Double amountSent) {

        // ------------------------------------------------------------------------------
        // -------- Balance --------
        // ------------------------------------------------------------------------------

        // Get the user balance
        log.info("Perform get user balance call");
        Double userBalance = this.getBalance(userId);
        log.info("User balance: " + userBalance);

        // Evaluate if the user has sufficient balance
        if (userBalance < amountSent) {
            log.error("User balance is insufficient. userBalance=" + userBalance);
            throw new WalletInsufficientBalanceException("Insufficient balance amount.");
        }

        // ------------------------------------------------------------------------------
        // -------- Recipient --------
        // ------------------------------------------------------------------------------

        //Get Recipient from db
        log.info("Looking for recipientId: " + recipientId);
        Recipient recipient = this.getRecipient(recipientId);

        if (recipient == null) {
            log.error("Recipient no found. recipientId=" + recipientId);
            throw new RecipientNotFoundException("Insufficient balance amount.");
        }

        log.info("Recipient found");

        // ------------------------------------------------------------------------------
        // -------- Fee logic process --------
        // ------------------------------------------------------------------------------
        log.info("Amount to be sent: " + amountSent);
        Double transactionFee = amountSent * Objects.requireNonNullElse(feePercentageVal, 0.10);
        log.info("10% transaction fee to apply: " + transactionFee);
        Double recipientGets = amountSent - transactionFee;
        log.info("Recipient gets: " + recipientGets);

        // ------------------------------------------------------------------------------
        // -------- Update Wallet process --------
        // ------------------------------------------------------------------------------
        try {
            log.info("Perform create wallet transaction call");
            WalletTransactionRequestDto walletTransactionRequestDto = this.buildWalletTransactionRequestDto(userId, recipientGets, true);
            this.createWalletTransaction(walletTransactionRequestDto);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound | HttpServerErrorException.InternalServerError ex) {
            log.error("Error when performing create wallet transaction call: " + ex.getMessage());
            throw new WalletrRequestException(ex.getMessage());
        }
        log.info("Success in performing create wallet transaction call");

        // ------------------------------------------------------------------------------
        // -------- Transaction process --------
        // ------------------------------------------------------------------------------

        Status transactionStatus = Status.IN_PROGRESS;
        //Set transaction
        log.info("Build the transaction object");
        Transaction transaction = this.buildTransaction(amountSent, transactionFee, recipientGets, recipient, txMsgDefault, transactionStatus);
        // Save Transaction in db
        Transaction savedTransaction = transactionDao.save(transaction);
        log.info("Transaction crated and saved: transactionId=" + savedTransaction.getId());

        // ------------------------------------------------------------------------------
        // -------- Payment process --------
        // ------------------------------------------------------------------------------

        try {
            // Build PaymentRequestDto
            log.info("Build the PaymentRequestDto object.");
            PaymentRequestDto paymentRequestDto = this.buildPaymentRequestDto(savedTransaction);
            // Send the payment request to 3rd. party.
            log.info("Perform create payment in provider call");
            this.performPayment(paymentRequestDto);
        } catch (HttpClientErrorException.BadRequest | HttpServerErrorException.InternalServerError ex) {
            log.error("Error performing create payment in provider call: " + ex.getMessage());

            log.info("Transaction with status FAILED");
            transaction.setStatus(Status.FAILED);
            transactionDao.save(transaction);

            //Proceed with refund
            log.info("Proceed to refund");
            this.buildWalletTransactionRequestDto(userId, recipientGets, false);
            transaction.setStatus(Status.REFUNDED);
            transaction.setMessage(txMsgRefund);
            transactionDao.save(transaction);

            throw new PaymentRequestException("Error performing payment: " + ex.getStatusCode(), ex);

        }

        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsByRecipientId(Long recipientId, Pageable pageable) {
        return transactionDao.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    private WalletTransactionRequestDto buildWalletTransactionRequestDto(Long userId, Double amount, boolean isWithdraw) {
        if (isWithdraw) {
            amount *= -1;
        }

        return WalletTransactionRequestDto.builder()
            .userId(userId)
            .amount(amount)
            .build();
    }

    private void createWalletTransaction(WalletTransactionRequestDto walletTransactionRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WalletTransactionRequestDto> requestEntity = new HttpEntity<>(walletTransactionRequestDto, headers);

        ResponseEntity<WalletTransactionResponseDto> responseEntity = restTemplate.postForEntity(walletTransactionsUrl, requestEntity,
            WalletTransactionResponseDto.class);

        responseEntity.getBody();
    }

    private Recipient getRecipient(Long recipientId) {
        return recipientDao.findById(recipientId).orElse(null);
    }

    private Double getBalance(Long userId) {
        String sbUrl = walletBalanceUrl
            + "?"
            + "user_id"
            + "="
            + userId;

        ResponseEntity<BalanceResponseDto> response = restTemplate.getForEntity(sbUrl, BalanceResponseDto.class);
        return Objects.requireNonNull(response.getBody()).getBalance();
    }

    private Transaction buildTransaction(Double amountSent, Double transactionFee, Double recipientGets, Recipient recipient,
        String message, Status transactionStatus) {

        Transaction transaction = new Transaction();
        transaction.setTransactionFee(transactionFee);
        transaction.setAmountSent(amountSent);
        transaction.setRecipientGets(recipientGets);
        transaction.setStatus(transactionStatus);
        transaction.setRecipient(recipient);
        transaction.setMessage(message);

        return transaction;
    }

    private PaymentRequestDto buildPaymentRequestDto(Transaction tx) {

        SourceInformationDto sourceInformationDto = SourceInformationDto.builder()
            .name(sourceInfoName)
            .build();

        AccountDto companyAccountDto = AccountDto.builder()
            .accountNumber(companyAccountNUmber)
            .currency(defaultCurrency)
            .routingNumber(companyRoutingNumber)
            .build();

        SourceDto sourceDto = SourceDto.builder()
            .type(companySourceType)
            .sourceInformation(sourceInformationDto)
            .account(companyAccountDto)
            .build();

        AccountDto recipientAccountDto = AccountDto.builder()
            .accountNumber(tx.getRecipient().getAccountNumber())
            .currency(defaultCurrency)
            .routingNumber(tx.getRecipient().getRoutingNumber())
            .build();

        DestinationDto destinationDto = DestinationDto.builder()
            .name(tx.getRecipient().getFirstName().concat(" ").concat(tx.getRecipient().getLastName()))
            .account(recipientAccountDto)
            .build();

        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
            .source(sourceDto)
            .destination(destinationDto)
            .amount(tx.getAmountSent())
            .build();

        return paymentRequestDto;
    }

    private void performPayment(PaymentRequestDto paymentRequestDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PaymentRequestDto> requestEntity = new HttpEntity<>(paymentRequestDto, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(paymentExternalApiUrl);

        // Send the POST request and retrieve the response
        ResponseEntity<PaymentResponseDto> responseEntity = restTemplate.exchange(
            builder.toUriString(),
            HttpMethod.POST,
            requestEntity,
            PaymentResponseDto.class
        );

        responseEntity.getBody();
    }

}
