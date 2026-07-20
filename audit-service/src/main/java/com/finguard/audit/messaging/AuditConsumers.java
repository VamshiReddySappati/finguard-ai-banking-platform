package com.finguard.audit.messaging;
import com.fasterxml.jackson.databind.ObjectMapper;import com.finguard.audit.service.AuditService;import com.finguard.events.*;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.stereotype.Component;
@Component public class AuditConsumers {
 private final ObjectMapper mapper;private final AuditService audit;public AuditConsumers(ObjectMapper mapper,AuditService audit){this.mapper=mapper;this.audit=audit;}
 @KafkaListener(topics="transaction.initiated.v1",groupId="audit-initiated")public void initiated(String payload)throws Exception{var e=mapper.readValue(payload,TransactionInitiatedEvent.class);audit.record(e.eventId(),e.transactionId(),"TRANSACTION_INITIATED",payload);}
 @KafkaListener(topics="fraud.decision.v1",groupId="audit-fraud")public void fraud(String payload)throws Exception{var e=mapper.readValue(payload,FraudDecisionEvent.class);audit.record(e.eventId(),e.transactionId(),"FRAUD_DECISION_"+e.decision(),payload);}
 @KafkaListener(topics="transaction.completed.v1",groupId="audit-completed")public void completed(String payload)throws Exception{var e=mapper.readValue(payload,TransactionCompletedEvent.class);audit.record(e.eventId(),e.transactionId(),"TRANSACTION_COMPLETED",payload);}
 @KafkaListener(topics="transaction.failed.v1",groupId="audit-failed")public void failed(String payload)throws Exception{var e=mapper.readValue(payload,TransactionFailedEvent.class);audit.record(e.eventId(),e.transactionId(),"TRANSACTION_FAILED",payload);}
}
