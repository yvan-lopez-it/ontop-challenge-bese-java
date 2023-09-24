package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.builders.PaymentRequestDtoBuilder;
import com.ontop.challenge.backend.apirest.dto.payment.request.PaymentRequestDto;
import com.ontop.challenge.backend.apirest.dto.payment.response.PaymentResponseDto;
import com.ontop.challenge.backend.apirest.entities.TransactionEntity;
import com.ontop.challenge.backend.apirest.exceptions.payment.PaymentRequestException;
import com.ontop.challenge.backend.apirest.services.IPaymentService;
import org.jetbrains.annotations.NotNull;
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
public class PaymentServiceImpl implements IPaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final RestTemplate restTemplate;
    private final String paymentExternalApiUrl;


    @Autowired
    public PaymentServiceImpl(RestTemplate restTemplate, @Value("${payment.external.api.url}") String paymentExternalApiUrl) {
        this.restTemplate = restTemplate;
        this.paymentExternalApiUrl = paymentExternalApiUrl;
    }

    @Override
    public void performPayment(@NotNull TransactionEntity transactionEntity) {
        PaymentRequestDto paymentRequestDto = PaymentRequestDtoBuilder.buildPaymentRequestDto(transactionEntity);
        this.sendPaymentRequest(paymentRequestDto, transactionEntity);
    }

    private void sendPaymentRequest(PaymentRequestDto paymentRequestDto, @NotNull TransactionEntity transactionEntity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<PaymentRequestDto> requestEntity = new HttpEntity<>(paymentRequestDto, headers);

            ResponseEntity<PaymentResponseDto> responseEntity = restTemplate.exchange(
                paymentExternalApiUrl,
                HttpMethod.POST,
                requestEntity,
                PaymentResponseDto.class
            );

            log.info("Payment request sent successfully for transactionEntity ID: {}", transactionEntity.getId());
            PaymentResponseDto paymentResponseDto = responseEntity.getBody();

            if (paymentResponseDto != null) {
                log.info("Payment response received: {}", paymentResponseDto);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error sending payment request for transactionEntity ID: {}. Error message: {}", transactionEntity.getId(), e.getMessage());
            throw new PaymentRequestException("Error sending payment request", e);
        }


    }
}
