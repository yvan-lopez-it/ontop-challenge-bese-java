package com.ontop.challenge.backend.apirest.dto.recipient;

import com.ontop.challenge.backend.apirest.dto.transaction.TransactionDto;
import java.util.List;
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
    private List<TransactionDto> transactions;

}
