package com.finguard.fraud.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finguard.events.FraudDecisionEvent;
import com.finguard.fraud.domain.FraudDecisionRecord;
import com.finguard.fraud.domain.FraudDecisionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class DecisionPublisher {
    private static final Logger log = LoggerFactory.getLogger(DecisionPublisher.class);

    private final FraudDecisionRepository decisions;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;

    public DecisionPublisher(FraudDecisionRepository decisions, KafkaTemplate<String, String> kafka,
                             ObjectMapper mapper) {
        this.decisions = decisions;
        this.kafka = kafka;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelayString = "${outbox.publish-delay-ms:1000}")
    @Transactional
    public void publish() {
        for (FraudDecisionRecord record : decisions.findTop100ByPublishedFalseOrderByCreatedAtAsc()) {
            try {
                FraudDecisionEvent event = new FraudDecisionEvent(
                        record.getEventId(), record.getTransactionId(), record.getDecision(),
                        record.getRiskScore(), record.getReasons(), Instant.now());
                kafka.send("fraud.decision.v1", record.getTransactionId().toString(),
                                mapper.writeValueAsString(event))
                        .get(10, TimeUnit.SECONDS);
                record.markPublished();
                decisions.save(record);
            } catch (Exception exception) {
                log.warn("Decision publish failed for {}: {}",
                        record.getTransactionId(), exception.getMessage());
                break;
            }
        }
    }
}
