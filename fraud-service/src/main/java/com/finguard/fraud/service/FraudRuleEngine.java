package com.finguard.fraud.service;
import com.finguard.events.*;import org.springframework.stereotype.Component;import java.math.BigDecimal;import java.util.*;
@Component public class FraudRuleEngine {
 public RuleResult evaluate(TransactionInitiatedEvent event,long transfersLastMinute){
  List<String> reasons=new ArrayList<>();int score=5;
  if(event.sourceAccountId().equals(event.destinationAccountId())){reasons.add("Source and destination accounts are identical");return new RuleResult(FraudDecisionType.REJECTED,100,reasons);}
  if(event.amount().compareTo(new BigDecimal("10000.00"))>0){reasons.add("Amount exceeds the automatic transfer threshold");score+=80;}
  if(transfersLastMinute>5){reasons.add("High transfer velocity detected");score+=70;}
  if(score>=75)return new RuleResult(FraudDecisionType.REJECTED,Math.min(score,100),reasons);
  if(event.amount().compareTo(new BigDecimal("5000.00"))>=0){reasons.add("Large transfer requires analyst approval");score+=50;return new RuleResult(FraudDecisionType.FLAGGED,score,reasons);}
  reasons.add("No high-risk rule matched");return new RuleResult(FraudDecisionType.APPROVED,score,reasons);
 }
}
