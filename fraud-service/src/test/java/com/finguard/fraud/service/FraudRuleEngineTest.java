package com.finguard.fraud.service;import com.finguard.events.*;import org.junit.jupiter.api.Test;import java.math.BigDecimal;import java.time.Instant;import java.util.UUID;import static org.assertj.core.api.Assertions.assertThat;
class FraudRuleEngineTest {private final FraudRuleEngine rules=new FraudRuleEngine();private TransactionInitiatedEvent event(String amount){return new TransactionInitiatedEvent(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),new BigDecimal(amount),"USD","user",Instant.now());}
 @Test void approvesNormalTransfer(){assertThat(rules.evaluate(event("250.00"),1).decision()).isEqualTo(FraudDecisionType.APPROVED);}
 @Test void flagsLargeTransfer(){assertThat(rules.evaluate(event("5000.00"),1).decision()).isEqualTo(FraudDecisionType.FLAGGED);}
 @Test void rejectsVeryLargeTransfer(){assertThat(rules.evaluate(event("15000.00"),1).decision()).isEqualTo(FraudDecisionType.REJECTED);}}
