package com.ontop.challenge.backend.apirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "recipients")
public class Recipient implements Serializable {

    @Serial
    private static final long serialVersionUID = 4051255552006179927L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The first name can't be empty")
    @Size(min = 3, max = 15, message = "The size of the name must be between 2 and 15 characters long")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "The last name can't be empty")
    @Size(min = 2, max = 15, message = "The size of the last name must be between 2 and 15 characters long")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "The routing number can't be empty")
    @Size(min = 9, max = 9, message = "The size of the routing number must be 9 characters long")
    @Column(nullable = false, unique = true)
    private String routingNumber;

    @NotBlank(message = "The national id number can't be empty")
    @Size(min = 6, message = "The size of the national id number must be between 2 and 15 characters long")
    @Column(nullable = false, unique = true)
    private String nationalIdNumber;

    @NotBlank(message = "The account number can't be empty")
    @Column(nullable = false, unique = true)
    @Size(min = 10, message = "The size of the account number must be at least 9 characters long")
    private String accountNumber;

    @NotBlank(message = "The bank name can't be empty")
    @Size(min = 3, max = 20, message = "The size of the bank name must be between 3 and 20 characters long")
    @Column(nullable = false)
    private String bankName;

    @OneToMany(mappedBy = "recipient")
    @JsonIgnoreProperties({"recipient", "hibernateLazyInitializer", "handler"})
    private List<Transaction> transactions = new ArrayList<>();

}
