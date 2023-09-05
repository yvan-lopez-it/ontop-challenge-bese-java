package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.dto.BalanceResponseDto;
import com.ontop.challenge.backend.apirest.dto.wallet.WalletTransactionRequestDto;
import com.ontop.challenge.backend.apirest.dto.wallet.WalletTransactionResponseDto;
import com.ontop.challenge.backend.apirest.exceptions.wallet.WalletrRequestException;
import com.ontop.challenge.backend.apirest.services.IWalletService;
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

@Service
public class WalletServiceImpl implements IWalletService {

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
    public Double getBalance(Long userId) {
        return this.fetchBalanceFromWallet(userId);
    }

    @Override
    public void updateWallet(Long userId, Double amount, boolean isWithdraw) {
        try {
            WalletTransactionRequestDto walletTransactionRequestDto = buildWalletTransactionRequestDto(userId, amount, isWithdraw);
            createWalletTransaction(walletTransactionRequestDto);
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound | HttpServerErrorException.InternalServerError ex) {
            throw new WalletrRequestException(ex.getMessage());
        }
    }

    private Double fetchBalanceFromWallet(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = walletBalanceUrl + "?user_id=" + userId;

        ResponseEntity<BalanceResponseDto> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            BalanceResponseDto.class
        );

        return Objects.requireNonNull(responseEntity.getBody()).getBalance();
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

        HttpEntity<WalletTransactionRequestDto> requestEntity = new HttpEntity<>(walletTransactionRequestDto, headers);

        ResponseEntity<WalletTransactionResponseDto> responseEntity = restTemplate.postForEntity(walletTransactionsUrl, requestEntity,
            WalletTransactionResponseDto.class);

        responseEntity.getBody();
    }
}
