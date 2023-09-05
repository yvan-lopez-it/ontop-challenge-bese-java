package com.ontop.challenge.backend.apirest.builders;

import com.ontop.challenge.backend.apirest.dto.payment.request.AccountDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.DestinationDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.PaymentRequestDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.SourceDto;
import com.ontop.challenge.backend.apirest.dto.payment.request.SourceInformationDto;
import com.ontop.challenge.backend.apirest.models.Transaction;

public class PaymentRequestDtoBuilder {

    //For the sake of the challenge demo, let's consider a unique company: ONTOP INC
    private static final String SOURCE_INFO_NAME = "ONTOP INC";
    private static final String COMPANY_ACCOUNT_NUMBER = "0245253419";
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String COMPANY_ROUTING_NUMBER = "028444018";
    private static final String COMPANY_SOURCE_TYPE = "COMPANY";


    public static PaymentRequestDto buildPaymentRequestDto(Transaction transaction) {
        SourceInformationDto sourceInformationDto = SourceInformationDto.builder()
            .name(SOURCE_INFO_NAME)
            .build();

        AccountDto companyAccountDto = AccountDto.builder()
            .accountNumber(COMPANY_ACCOUNT_NUMBER)
            .currency(DEFAULT_CURRENCY)
            .routingNumber(COMPANY_ROUTING_NUMBER)
            .build();

        SourceDto sourceDto = SourceDto.builder()
            .type(COMPANY_SOURCE_TYPE)
            .sourceInformation(sourceInformationDto)
            .account(companyAccountDto)
            .build();

        AccountDto recipientAccountDto = AccountDto.builder()
            .accountNumber(transaction.getRecipient().getAccountNumber())
            .currency(DEFAULT_CURRENCY)
            .routingNumber(transaction.getRecipient().getRoutingNumber())
            .build();

        DestinationDto destinationDto = DestinationDto.builder()
            .name(transaction.getRecipient()
                .getFirstName()
                .concat(" ")
                .concat(transaction.getRecipient().getLastName())
            )
            .account(recipientAccountDto)
            .build();

        return PaymentRequestDto.builder()
            .source(sourceDto)
            .destination(destinationDto)
            .amount(transaction.getAmountSent())
            .build();
    }

}
