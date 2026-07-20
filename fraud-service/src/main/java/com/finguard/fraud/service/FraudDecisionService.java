package com.finguard.fraud.service;
import com.finguard.events.*;import com.finguard.fraud.domain.*;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;import java.util.*;
@Service public class FraudDecisionService {
 private final FraudDecisionRepository decisions;private final FraudRuleEngine rules;private final VelocityCounter velocity;
 public FraudDecisionService(FraudDecisionRepository decisions,FraudRuleEngine rules,VelocityCounter velocity){this.decisions=decisions;this.rules=rules;this.velocity=velocity;}
 @Transactional public void evaluate(TransactionInitiatedEvent event){if(decisions.findByTransactionId(event.transactionId()).isPresent())return;RuleResult result=rules.evaluate(event,velocity.increment(event.sourceAccountId()));decisions.save(new FraudDecisionRecord(event.transactionId(),result.decision(),result.riskScore(),result.reasons()));}
 public List<FraudDecisionRecord> all(){return decisions.findAllByOrderByCreatedAtDesc();}
 public FraudDecisionRecord one(UUID transactionId){return decisions.findByTransactionId(transactionId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Fraud decision not found"));}
 @Transactional public FraudDecisionRecord resolve(UUID transactionId,FraudDecisionType resolution,String note){if(resolution==FraudDecisionType.FLAGGED)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Resolution must be APPROVED or REJECTED");FraudDecisionRecord r=one(transactionId);if(r.getDecision()!=FraudDecisionType.FLAGGED)throw new ResponseStatusException(HttpStatus.CONFLICT,"Only flagged decisions can be resolved");r.update(resolution,resolution==FraudDecisionType.REJECTED?100:35,List.of("Manual analyst decision: "+note));return decisions.save(r);}
}
