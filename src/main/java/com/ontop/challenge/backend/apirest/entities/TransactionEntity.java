package com.ontop.challenge.backend.apirest.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ontop.challenge.backend.apirest.enums.TransactionStatus;
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
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions", schema = "public")
public class TransactionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4985822431604944901L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The amount sent can't be null")
    @Column(nullable = false)
    private Double amountSent;

    @NotNull(message = "The transaction fee can't be null")
    @Column(nullable = false)
    private Double transactionFee;

    @NotNull(message = "The recipientEntity gets can't be null")
    @Column(nullable = false)
    private Double recipientGets;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double refundedAmount;

    @Column(nullable = true)
    private Long associatedTransactionId;

    @NotNull(message = "The user id can't be null")
    @Column(nullable = false)
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @NotNull(message = "The status can't be null")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @NotNull(message = "The message can't be null")
    @Column(nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    @JsonIgnoreProperties(value = {"transactions", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    private RecipientEntity recipient;
}
