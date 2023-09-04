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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TransactionServiceImpl implements ITransactionService {

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
        Double userBalance = this.getBalance(userId);

        // Evaluate if the user has sufficient balance
        if (userBalance < amountSent) {
            System.err.println("User balance is insufficient. userBalance=" + userBalance);
            throw new WalletInsufficientBalanceException("Insufficient balance amount.");
        }

        // ------------------------------------------------------------------------------
        // -------- Recipient --------
        // ------------------------------------------------------------------------------

        //Get Recipient from db
        Recipient recipient = this.getRecipient(recipientId);

        if (recipient == null) {
            System.err.println("Recipient no found. recipientId=" + recipientId);
            throw new RecipientNotFoundException("Insufficient balance amount.");
        }

        // ------------------------------------------------------------------------------
        // -------- Fee logic process --------
        // ------------------------------------------------------------------------------
        Double transactionFee = amountSent * feePercentageVal / 100.00;
        Double recipientGets = amountSent - transactionFee;

        // ------------------------------------------------------------------------------
        // -------- Update Wallet process --------
        // ------------------------------------------------------------------------------
        try {
            WalletTransactionRequestDto walletTransactionRequestDto = this.buildWalletTransactionRequestDto(userId, recipientGets, true);
            this.createWalletTransaction(walletTransactionRequestDto);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound | HttpServerErrorException.InternalServerError ex) {
            System.err.println(ex.getMessage());
            throw new WalletrRequestException(ex.getMessage());
        }

        // ------------------------------------------------------------------------------
        // -------- Transaction process --------
        // ------------------------------------------------------------------------------

        Status transactionStatus = Status.IN_PROGRESS;
        //Set transaction
        Transaction transaction = this.buildTransaction(amountSent, transactionFee, recipientGets, recipient, txMsgDefault, transactionStatus);
        // Save Transaction in db
        Transaction savedTx = transactionDao.save(transaction);

        // ------------------------------------------------------------------------------
        // -------- Payment process --------
        // ------------------------------------------------------------------------------

        try {
            // Build PaymentRequestDto
            PaymentRequestDto paymentRequestDto = this.buildPaymentRequestDto(savedTx);
            // Send the payment request to 3rd. party.
            this.performPayment(paymentRequestDto);
        } catch (HttpClientErrorException.BadRequest | HttpServerErrorException.InternalServerError ex) {
            System.err.println(ex.getMessage());

            transaction.setStatus(Status.FAILED);
            transactionDao.save(transaction);

            //Proceed with refund
            this.buildWalletTransactionRequestDto(userId, recipientGets, false);
            transaction.setStatus(Status.REFUNDED);
            transaction.setMessage(txMsgRefund);
            transactionDao.save(transaction);

            throw new PaymentRequestException("Error performing payment: " + ex.getStatusCode(), ex);

        }

        return savedTx;
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
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(walletBalanceUrl)
            .append("?")
            .append("user_id")
            .append("=")
            .append(userId);

        ResponseEntity<BalanceResponseDto> response = restTemplate.getForEntity(sbUrl.toString(), BalanceResponseDto.class);
        return Objects.requireNonNull(response.getBody()).getBalance();
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

    private PaymentRequestDto buildPaymentRequestDto(Transaction tx) {

        //TODO: Company info in another place maybe in props
        SourceInformationDto sourceInformationDto = SourceInformationDto.builder()
            .name("ONTOP INC")
            .build();

        AccountDto companyAccountDto = AccountDto.builder()
            .accountNumber("0245253419")
            .currency("USD")
            .routingNumber("028444018")
            .build();

        SourceDto sourceDto = SourceDto.builder()
            .type("COMPANY")
            .sourceInformation(sourceInformationDto)
            .account(companyAccountDto)
            .build();

        AccountDto recipientAccountDto = AccountDto.builder()
            .accountNumber(tx.getRecipient().getAccountNumber())
            .currency("USD") //For the sake of this demo, let's use USD currency
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

        //FIXME
        System.out.println("paymentRequestDto: " + paymentRequestDto);

        return paymentRequestDto;
    }

    private void performPayment(PaymentRequestDto paymentRequestDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP request entity with the DTO as the request body
        HttpEntity<PaymentRequestDto> requestEntity = new HttpEntity<>(paymentRequestDto, headers);

        // Build the URI with parameters (if any)
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(paymentExternalApiUrl);

        // Send the POST request and retrieve the response
        ResponseEntity<PaymentResponseDto> responseEntity = restTemplate.exchange(
            builder.toUriString(),
            HttpMethod.POST,
            requestEntity,
            PaymentResponseDto.class
        );

        // Extract and return the response body
        responseEntity.getBody();
    }

}
