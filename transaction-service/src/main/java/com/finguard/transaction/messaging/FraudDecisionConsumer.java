package com.finguard.transaction.messaging;
import com.fasterxml.jackson.databind.ObjectMapper;import com.finguard.events.FraudDecisionEvent;import com.finguard.transaction.service.TransferProcessor;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.stereotype.Component;
@Component public class FraudDecisionConsumer {
 private final ObjectMapper mapper;private final TransferProcessor processor;public FraudDecisionConsumer(ObjectMapper mapper,TransferProcessor processor){this.mapper=mapper;this.processor=processor;}
 @KafkaListener(topics="fraud.decision.v1",groupId="transaction-service") public void consume(String payload)throws Exception{processor.handle(mapper.readValue(payload,FraudDecisionEvent.class));}
}
