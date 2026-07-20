package com.finguard.fraud.domain;

import com.finguard.events.FraudDecisionType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "fraud_decisions",
        uniqueConstraints = @UniqueConstraint(name = "uk_fraud_transaction", columnNames = "transaction_id"),
        indexes = @Index(name = "idx_fraud_unpublished", columnList = "published,created_at"))
public class FraudDecisionRecord {
    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudDecisionType decision;

    @Column(name = "risk_score", nullable = false)
    private int riskScore;

    @Column(nullable = false, length = 1000)
    private String reasons;

    @Column(nullable = false)
    private boolean published;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected FraudDecisionRecord() {
    }

    public FraudDecisionRecord(UUID transactionId, FraudDecisionType decision,
                               int riskScore, List<String> reasons) {
        this.id = UUID.randomUUID();
        this.transactionId = transactionId;
        this.createdAt = Instant.now();
        update(decision, riskScore, reasons);
    }

    public void update(FraudDecisionType decision, int riskScore, List<String> reasons) {
        this.eventId = UUID.randomUUID();
        this.decision = decision;
        this.riskScore = riskScore;
        this.reasons = String.join(" | ", reasons);
        this.published = false;
        this.publishedAt = null;
        this.updatedAt = Instant.now();
    }

    public void markPublished() {
        published = true;
        publishedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getEventId() { return eventId; }
    public UUID getTransactionId() { return transactionId; }
    public FraudDecisionType getDecision() { return decision; }
    public int getRiskScore() { return riskScore; }
    public List<String> getReasons() {
        return reasons == null || reasons.isBlank()
                ? List.of() : Arrays.asList(reasons.split(" \\| "));
    }
    public boolean isPublished() { return published; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
