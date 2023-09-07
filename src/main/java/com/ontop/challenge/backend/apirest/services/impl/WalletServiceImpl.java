package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.dto.BalanceResponseDto;
import com.ontop.challenge.backend.apirest.dto.wallet.WalletTransactionRequestDto;
import com.ontop.challenge.backend.apirest.dto.wallet.WalletTransactionResponseDto;
import com.ontop.challenge.backend.apirest.exceptions.wallet.BalanceRequestException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletInsufficientBalanceException;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletRequestException;
import com.ontop.challenge.backend.apirest.services.IWalletService;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class WalletServiceImpl implements IWalletService {

    private final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final String walletBalanceUrl;
    private final String walletTransactionsUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public WalletServiceImpl(
        @Value("${wallet.balance.url}") String walletBalanceUrl,
        @Value("${wallet.transaction.url}") String walletTransactionsUrl,
        RestTemplate restTemplate) {
        this.walletBalanceUrl = walletBalanceUrl;
        this.walletTransactionsUrl = walletTransactionsUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void checkBalance(Long userId, Double amountSent) {

        log.info("Fetching balance from Wallet API for user ID: {}", userId);

        Double balance = this.fetchBalanceFromWallet(userId);

        this.ensureSufficientBalance(balance, amountSent);

        log.info("Balance retrieved successfully: {}", balance);
    }

    @Override
    public void updateWallet(Long userId, Double amount, boolean isWithdraw) {
        log.info("Updating wallet for user ID: {}. Amount: {}. IsWithdraw: {}", userId, amount, isWithdraw);

        try {
            WalletTransactionRequestDto walletTransactionRequestDto = buildWalletTransactionRequestDto(userId, amount, isWithdraw);
            createWalletTransaction(walletTransactionRequestDto);
            log.info("Wallet updated successfully for user ID: {}", userId);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound | HttpServerErrorException.InternalServerError e) {
            log.error("Error updating wallet for user ID: {}. Error message: {}", userId, e.getMessage());
            throw new WalletRequestException(e.getMessage(), e);
        }
    }

    private void ensureSufficientBalance(Double userBalance, Double amountSent) {
        if (userBalance < amountSent) {
            throw new WalletInsufficientBalanceException("Insufficient balance amount.");
        }
    }

    private Double fetchBalanceFromWallet(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String url = walletBalanceUrl + "?user_id=" + userId;

            ResponseEntity<BalanceResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BalanceResponseDto.class
            );

            return Objects.requireNonNull(responseEntity.getBody()).getBalance();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching balance from wallet request for user ID: {}. Error message: {}", userId, e.getMessage());
            throw new BalanceRequestException("Error fetching balance from wallet request", e);
        }

    }

    private WalletTransactionRequestDto buildWalletTransactionRequestDto(Long userId, Double amount, boolean isWithdraw) {
        amount = isWithdraw ? -amount : amount;

        return WalletTransactionRequestDto.builder()
            .userId(userId)
            .amount(amount)
            .build();
    }

    private void createWalletTransaction(WalletTransactionRequestDto walletTransactionRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<WalletTransactionRequestDto> requestEntity = new HttpEntity<>(walletTransactionRequestDto, headers);

            ResponseEntity<WalletTransactionResponseDto> responseEntity =
                restTemplate.postForEntity(
                    walletTransactionsUrl,
                    requestEntity,
                    WalletTransactionResponseDto.class
                );

            log.info("Wallet transaction request sent successfully for user ID: {}", walletTransactionRequestDto.getUserId());
            WalletTransactionResponseDto walletTransactionResponseDto = responseEntity.getBody();

            if (walletTransactionResponseDto != null) {
                log.info("Wallet transaction response received: {}", walletTransactionResponseDto);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error sending wallet transaction request for user ID: {}. Error message: {}", walletTransactionRequestDto.getUserId(), e.getMessage());
            throw new WalletRequestException("Error sending wallet transaction request", e);
        }


    }
}
