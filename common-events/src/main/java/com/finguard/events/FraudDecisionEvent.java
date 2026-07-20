package com.finguard.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FraudDecisionEvent(
        UUID eventId,
        UUID transactionId,
        FraudDecisionType decision,
        int riskScore,
        List<String> reasons,
        Instant occurredAt
) {}
