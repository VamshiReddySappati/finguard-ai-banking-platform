package com.finguard.audit.domain;
import jakarta.persistence.*;import java.time.Instant;import java.util.UUID;
@Entity @Table(name="audit_events",uniqueConstraints=@UniqueConstraint(name="uk_audit_event_id",columnNames="source_event_id"),indexes={@Index(name="idx_audit_transaction",columnList="transaction_id"),@Index(name="idx_audit_created",columnList="created_at")})
public class AuditEvent {
 @Id private UUID id;@Column(name="source_event_id",nullable=false) private UUID sourceEventId;@Column(name="transaction_id",nullable=false) private UUID transactionId;@Column(nullable=false,length=80) private String eventType;@Lob @Column(nullable=false,columnDefinition="TEXT") private String payload;@Column(name="created_at",nullable=false) private Instant createdAt;
 protected AuditEvent(){}
 public AuditEvent(UUID sourceEventId,UUID transactionId,String eventType,String payload){this.id=UUID.randomUUID();this.sourceEventId=sourceEventId;this.transactionId=transactionId;this.eventType=eventType;this.payload=payload;this.createdAt=Instant.now();}
 public UUID getId(){return id;}public UUID getSourceEventId(){return sourceEventId;}public UUID getTransactionId(){return transactionId;}public String getEventType(){return eventType;}public String getPayload(){return payload;}public Instant getCreatedAt(){return createdAt;}
}
