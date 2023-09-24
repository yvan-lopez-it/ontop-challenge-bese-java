package com.ontop.challenge.backend.apirest.dto.recipient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipientDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String routingNumber;
    private String nationalIdNumber;
    private String accountNumber;
    private String bankName;

}
