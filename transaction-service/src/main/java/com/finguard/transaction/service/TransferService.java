package com.finguard.transaction.service;
import com.fasterxml.jackson.core.JsonProcessingException;import com.fasterxml.jackson.databind.ObjectMapper;import com.finguard.events.TransactionInitiatedEvent;import com.finguard.transaction.api.*;import com.finguard.transaction.client.*;import com.finguard.transaction.domain.*;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;import java.time.Instant;import java.util.*;
@Service
public class TransferService {
 public static final String INITIATED_TOPIC="transaction.initiated.v1";
 private final BankTransactionRepository transactions;private final OutboxRepository outbox;private final AccountClient accounts;private final ObjectMapper mapper;
 public TransferService(BankTransactionRepository transactions,OutboxRepository outbox,AccountClient accounts,ObjectMapper mapper){this.transactions=transactions;this.outbox=outbox;this.accounts=accounts;this.mapper=mapper;}
 @Transactional
 public TransactionResponse initiate(String userId,boolean admin,String idempotencyKey,TransferRequest request){
  if(idempotencyKey==null||idempotencyKey.isBlank()||idempotencyKey.length()>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A valid Idempotency-Key header is required");
  Optional<BankTransaction> existing=transactions.findByInitiatedByAndIdempotencyKey(userId,idempotencyKey);if(existing.isPresent()) return toResponse(existing.get());
  if(request.sourceAccountId().equals(request.destinationAccountId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Source and destination accounts must differ");
  AccountSnapshot source=accounts.get(request.sourceAccountId());AccountSnapshot destination=accounts.get(request.destinationAccountId());
  if(!admin&&!source.ownerId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Source account does not belong to authenticated user");
  if(!source.currency().equals(destination.currency())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cross-currency transfers are not supported in this demo");
  UUID id=UUID.randomUUID();BankTransaction tx=transactions.save(new BankTransaction(id,source.id(),destination.id(),request.amount(),source.currency(),userId,idempotencyKey));
  TransactionInitiatedEvent event=new TransactionInitiatedEvent(UUID.randomUUID(),id,source.id(),destination.id(),request.amount(),source.currency(),userId,Instant.now());
  outbox.save(new OutboxEvent(id,INITIATED_TOPIC,json(event)));return toResponse(tx);
 }
 public List<TransactionResponse> mine(String userId){return transactions.findByInitiatedByOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();}
 public TransactionResponse one(UUID id,String userId,boolean admin){BankTransaction tx=transactions.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Transaction not found"));if(!admin&&!tx.getInitiatedBy().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Transaction does not belong to authenticated user");return toResponse(tx);}
 public List<TransactionResponse> all(){return transactions.findAll().stream().map(this::toResponse).toList();}
 String json(Object value){try{return mapper.writeValueAsString(value);}catch(JsonProcessingException e){throw new IllegalStateException("Could not serialize event",e);}}
 TransactionResponse toResponse(BankTransaction t){return new TransactionResponse(t.getId(),t.getSourceAccountId(),t.getDestinationAccountId(),t.getAmount(),t.getCurrency(),t.getStatus(),t.getFailureReason(),t.getCreatedAt(),t.getUpdatedAt());}
}
