package com.finguard.transaction.messaging;
import com.finguard.transaction.domain.*;import org.slf4j.*;import org.springframework.kafka.core.KafkaTemplate;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;import org.springframework.transaction.annotation.Transactional;import java.util.concurrent.TimeUnit;
@Component public class OutboxPublisher {
 private static final Logger log=LoggerFactory.getLogger(OutboxPublisher.class);private final OutboxRepository outbox;private final KafkaTemplate<String,String> kafka;
 public OutboxPublisher(OutboxRepository outbox,KafkaTemplate<String,String> kafka){this.outbox=outbox;this.kafka=kafka;}
 @Scheduled(fixedDelayString="${outbox.publish-delay-ms:1000}") @Transactional public void publish(){for(OutboxEvent event:outbox.findTop100ByPublishedFalseOrderByCreatedAtAsc()){try{kafka.send(event.getTopic(),event.getAggregateId().toString(),event.getPayload()).get(10, TimeUnit.SECONDS);event.markPublished();outbox.save(event);}catch(Exception ex){log.warn("Outbox publish failed for {}: {}",event.getId(),ex.getMessage());break;}}}
}
