package com.finguard.fraud.service;import com.finguard.events.FraudDecisionType;import java.util.List;
public record RuleResult(FraudDecisionType decision,int riskScore,List<String> reasons){}
