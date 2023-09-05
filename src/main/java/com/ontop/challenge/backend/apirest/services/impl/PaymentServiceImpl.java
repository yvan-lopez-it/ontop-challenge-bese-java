package com.ontop.challenge.backend.apirest.services.impl;

import com.ontop.challenge.backend.apirest.dto.payment.request.AccountDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.DestinationDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.PaymentRequestDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.SourceDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.SourceInformationDto;
import com.ontop.challenge.backend.apirest.dto.payment.response.PaymentResponseDto;
import com.ontop.challenge.backend.apirest.models.Transaction;
import com.ontop.challenge.backend.apirest.services.IPaymentService;
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
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final RestTemplate restTemplate;
    private final String paymentExternalApiUrl;
    private final String sourceInfoName;
    private final String companyAccountNUmber;
    private final String defaultCurrency;
    private final String companyRoutingNumber;
    private final String companySourceType;

    @Autowired
    public PaymentServiceImpl(RestTemplate restTemplate,
        @Value("${payment.external.api.url}") String paymentExternalApiUrl,
        @Value("${ontop.source.info.name}") String sourceInfoName,
        @Value("${ontop.account.number}") String companyAccountNUmber,
        @Value("${currency.default}") String defaultCurrency,
        @Value("${ontop.routing.number}") String companyRoutingNumber,
        @Value("${ontop.source.type}") String companySourceType) {
        this.restTemplate = restTemplate;
        this.paymentExternalApiUrl = paymentExternalApiUrl;
        this.sourceInfoName = sourceInfoName;
        this.companyAccountNUmber = companyAccountNUmber;
        this.defaultCurrency = defaultCurrency;
        this.companyRoutingNumber = companyRoutingNumber;
        this.companySourceType = companySourceType;
    }

    @Override
    public void performPayment(Transaction transaction) {
        PaymentRequestDto paymentRequestDto = this.buildPaymentRequestDto(transaction);
        this.sendPaymentRequest(paymentRequestDto);
    }

    private PaymentRequestDto buildPaymentRequestDto(Transaction transaction) {
        SourceInformationDto sourceInformationDto = SourceInformationDto.builder().name(sourceInfoName).build();

        AccountDto companyAccountDto = AccountDto.builder().accountNumber(companyAccountNUmber).currency(defaultCurrency).routingNumber(companyRoutingNumber)
            .build();

        SourceDto sourceDto = SourceDto.builder().type(companySourceType).sourceInformation(sourceInformationDto).account(companyAccountDto).build();

        AccountDto recipientAccountDto = AccountDto.builder().accountNumber(transaction.getRecipient().getAccountNumber()).currency(defaultCurrency)
            .routingNumber(transaction.getRecipient().getRoutingNumber()).build();

        DestinationDto destinationDto = DestinationDto.builder()
            .name(transaction.getRecipient().getFirstName().concat(" ").concat(transaction.getRecipient().getLastName())).account(recipientAccountDto).build();

        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder().source(sourceDto).destination(destinationDto).amount(transaction.getAmountSent())
            .build();

        return paymentRequestDto;
    }

    private void sendPaymentRequest(PaymentRequestDto paymentRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PaymentRequestDto> requestEntity = new HttpEntity<>(paymentRequestDto, headers);

        ResponseEntity<PaymentResponseDto> responseEntity = restTemplate.exchange(
            paymentExternalApiUrl,
            HttpMethod.POST,
            requestEntity,
            PaymentResponseDto.class
        );

        responseEntity.getBody();
    }
}
