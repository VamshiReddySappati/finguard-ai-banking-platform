package com.finguard.account.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "account_operations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_account_tx_type",
                columnNames = {"account_id", "transaction_id", "operation_type"}),
        indexes = @Index(name = "idx_operation_transaction", columnList = "transaction_id"))
public class AccountOperation {
    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AccountOperation() {
    }

    public AccountOperation(UUID accountId, UUID transactionId, OperationType type,
                            BigDecimal amount, BigDecimal balanceAfter) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.operationType = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = Instant.now();
    }
}
