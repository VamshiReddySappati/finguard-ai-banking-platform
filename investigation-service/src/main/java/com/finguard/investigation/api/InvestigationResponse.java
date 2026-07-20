package com.finguard.investigation.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvestigationResponse(
        UUID transactionId,
        String summary,
        List<String> recommendedActions,
        String provider,
        Instant generatedAt) {
}
