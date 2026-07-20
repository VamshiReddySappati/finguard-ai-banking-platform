package com.finguard.transaction.service;
import com.fasterxml.jackson.databind.ObjectMapper;import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;import com.finguard.transaction.api.TransferRequest;import com.finguard.transaction.client.*;import com.finguard.transaction.domain.*;import org.junit.jupiter.api.Test;import java.math.BigDecimal;import java.util.*;import static org.assertj.core.api.Assertions.assertThat;import static org.mockito.Mockito.*;
class TransferServiceTest {
 @Test void createsPendingTransactionAndOutboxEvent(){
  BankTransactionRepository repo=mock(BankTransactionRepository.class);OutboxRepository outbox=mock(OutboxRepository.class);AccountClient client=mock(AccountClient.class);ObjectMapper mapper=new ObjectMapper().registerModule(new JavaTimeModule());UUID source=UUID.randomUUID(),dest=UUID.randomUUID();
  when(client.get(source)).thenReturn(new AccountSnapshot(source,"user",new BigDecimal("100"),"USD"));when(client.get(dest)).thenReturn(new AccountSnapshot(dest,"other",BigDecimal.ZERO,"USD"));when(repo.save(any())).thenAnswer(i->i.getArgument(0));
  var response=new TransferService(repo,outbox,client,mapper).initiate("user",false,"key-1",new TransferRequest(source,dest,new BigDecimal("25.00")));
  assertThat(response.status()).isEqualTo(TransactionStatus.PENDING_REVIEW);verify(outbox).save(any(OutboxEvent.class));
 }
}
