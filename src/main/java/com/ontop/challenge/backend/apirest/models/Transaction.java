package com.ontop.challenge.backend.apirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction implements Serializable {

    @Serial
    private static final long serialVersionUID = 4985822431604944901L;

    private static final Double TX_FEE_PERCENTAGE = 10.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The available wallet funds can't be null")
    @Column(nullable = false)
    private Double availableWalletFunds;

    @NotNull(message = "The amount sent can't be null")
    @Column(nullable = false)
    private Double amountSent;

    @NotNull(message = "The transaction fee can't be null")
    @Column(nullable = false)
    private Double transactionFee;

    public void setTransactionFee(Double amountSent) {
        this.transactionFee = amountSent * TX_FEE_PERCENTAGE / 100.00;
    }

    @NotNull(message = "The recipient gets can't be null")
    @Column(nullable = false)
    private Double recipientGets;

    public void setRecipientGets(Double amountSent, Double transactionFee) {
        this.recipientGets = amountSent - transactionFee;
    }

    @NotNull(message = "The created at date can't be null")
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
    }

    // Define enum for transaction status
    public enum Status {
        COMPLETED, REFUNDED, FAILED, IN_PROGRESS
    }

    @NotNull(message = "The status can't be null")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    @JsonIgnoreProperties(value = {"transactions", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    private Recipient recipient;
}
