package com.finguard.events;

import java.time.Instant;
import java.util.UUID;

public record TransactionFailedEvent(
        UUID eventId,
        UUID transactionId,
        String reason,
        Instant occurredAt
) {}
