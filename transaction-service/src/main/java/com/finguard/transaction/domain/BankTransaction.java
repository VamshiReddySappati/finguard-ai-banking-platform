package com.finguard.transaction.domain;
import jakarta.persistence.*;import java.math.BigDecimal;import java.time.Instant;import java.util.UUID;
@Entity
@Table(name="bank_transactions",uniqueConstraints=@UniqueConstraint(name="uk_user_idempotency",columnNames={"initiated_by","idempotency_key"}),indexes={@Index(name="idx_tx_user",columnList="initiated_by"),@Index(name="idx_tx_status",columnList="status")})
public class BankTransaction {
 @Id private UUID id;
 @Column(name="source_account_id",nullable=false) private UUID sourceAccountId;
 @Column(name="destination_account_id",nullable=false) private UUID destinationAccountId;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
 @Column(nullable=false,length=3) private String currency;
 @Column(name="initiated_by",nullable=false) private String initiatedBy;
 @Column(name="idempotency_key",nullable=false,length=100) private String idempotencyKey;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private TransactionStatus status;
 @Column(name="failure_reason",length=500) private String failureReason;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 @Column(name="updated_at") private Instant updatedAt;
 @Version private long version;
 protected BankTransaction(){}
 public BankTransaction(UUID id,UUID source,UUID destination,BigDecimal amount,String currency,String initiatedBy,String idempotencyKey){this.id=id;this.sourceAccountId=source;this.destinationAccountId=destination;this.amount=amount;this.currency=currency;this.initiatedBy=initiatedBy;this.idempotencyKey=idempotencyKey;this.status=TransactionStatus.PENDING_REVIEW;this.createdAt=Instant.now();this.updatedAt=this.createdAt;}
 public void status(TransactionStatus status,String reason){this.status=status;this.failureReason=reason;this.updatedAt=Instant.now();}
 public UUID getId(){return id;} public UUID getSourceAccountId(){return sourceAccountId;} public UUID getDestinationAccountId(){return destinationAccountId;} public BigDecimal getAmount(){return amount;} public String getCurrency(){return currency;} public String getInitiatedBy(){return initiatedBy;} public String getIdempotencyKey(){return idempotencyKey;} public TransactionStatus getStatus(){return status;} public String getFailureReason(){return failureReason;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
