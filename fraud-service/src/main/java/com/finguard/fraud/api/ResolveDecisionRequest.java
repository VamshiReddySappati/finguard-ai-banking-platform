package com.finguard.fraud.api;import com.finguard.events.FraudDecisionType;import jakarta.validation.constraints.*;
public record ResolveDecisionRequest(@NotNull FraudDecisionType decision,@NotBlank @Size(max=300) String note){}
