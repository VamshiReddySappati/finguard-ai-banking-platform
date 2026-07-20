package com.finguard.investigation.service;

import com.finguard.investigation.api.InvestigationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InvestigationServiceTest {
    @Test
    void usesSafeFallbackWhenNoApiKeyIsConfigured() {
        InvestigationService service = new InvestigationService(
                RestClient.builder(), "https://api.openai.com/v1", "", "");
        var response = service.generate(new InvestigationRequest(
                UUID.randomUUID(), new BigDecimal("5000.00"), "USD", "FLAGGED", 55,
                List.of("Large transfer requires analyst approval"),
                List.of("TRANSACTION_INITIATED", "FRAUD_DECISION_FLAGGED")));

        assertThat(response.provider()).isEqualTo("deterministic-fallback");
        assertThat(response.summary()).contains("analyst must verify");
        assertThat(response.recommendedActions()).isNotEmpty();
    }
}
