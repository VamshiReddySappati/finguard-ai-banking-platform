package com.finguard.investigation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.finguard.investigation.api.InvestigationRequest;
import com.finguard.investigation.api.InvestigationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.*;

@Service
public class InvestigationService {
    private static final Logger log = LoggerFactory.getLogger(InvestigationService.class);
    private static final String SYSTEM_INSTRUCTION = """
            You are a banking fraud operations assistant. Produce a concise investigation brief
            using only the structured synthetic facts supplied. Do not invent customer identity,
            location, device, intent, policy, or evidence. State uncertainty explicitly. Do not
            approve, reject, or execute a transaction; a human analyst owns the final decision.
            """;

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public InvestigationService(RestClient.Builder builder,
                                @Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl,
                                @Value("${openai.api-key:}") String apiKey,
                                @Value("${openai.model:}") String model) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model == null ? "" : model.trim();
    }

    public InvestigationResponse generate(InvestigationRequest request) {
        String summary = deterministicSummary(request);
        String provider = "deterministic-fallback";

        if (!apiKey.isBlank() && !model.isBlank()) {
            try {
                summary = openAiSummary(request);
                provider = "openai:" + model;
            } catch (RestClientException | IllegalStateException exception) {
                log.warn("AI brief generation failed; using deterministic fallback: {}",
                        exception.getMessage());
            }
        }

        return new InvestigationResponse(request.transactionId(), summary,
                recommendations(request), provider, Instant.now());
    }

    private String openAiSummary(InvestigationRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("input", List.of(
                Map.of("role", "system", "content", SYSTEM_INSTRUCTION),
                Map.of("role", "user", "content", facts(request))));
        body.put("max_output_tokens", 450);

        JsonNode response = restClient.post()
                .uri("/responses")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        String text = extractOutputText(response);
        if (text == null || text.isBlank()) {
            throw new IllegalStateException("OpenAI response did not contain output text");
        }
        return text.trim();
    }

    static String extractOutputText(JsonNode response) {
        if (response == null) {
            return null;
        }
        JsonNode output = response.path("output");
        if (!output.isArray()) {
            return null;
        }
        for (JsonNode item : output) {
            for (JsonNode content : item.path("content")) {
                if ("output_text".equals(content.path("type").asText())) {
                    return content.path("text").asText();
                }
            }
        }
        return null;
    }

    private String facts(InvestigationRequest request) {
        return """
                Create an evidence-grounded case brief from these facts:
                transaction_id: %s
                amount: %s %s
                current_status: %s
                risk_score: %d/100
                rule_reasons: %s
                observed_event_types: %s
                """.formatted(request.transactionId(), request.amount(), request.currency(),
                request.status(), request.riskScore(), request.reasons(), request.eventTypes());
    }

    private String deterministicSummary(InvestigationRequest request) {
        String reasonText = request.reasons().isEmpty()
                ? "No risk reason was recorded."
                : "Recorded indicators: " + String.join("; ", request.reasons()) + ".";
        String eventText = request.eventTypes().isEmpty()
                ? "No audit events were supplied."
                : request.eventTypes().size() + " audit event type(s) were supplied.";
        return "Transaction %s is currently %s for %s %s with a risk score of %d/100. %s %s "
                .formatted(request.transactionId(), request.status(), request.currency(),
                        request.amount(), request.riskScore(), reasonText, eventText)
                + "This brief does not determine the final disposition; an analyst must verify the evidence.";
    }

    private List<String> recommendations(InvestigationRequest request) {
        List<String> actions = new ArrayList<>();
        actions.add("Verify the transaction state against the audit event sequence.");
        if (request.riskScore() >= 75) {
            actions.add("Escalate for enhanced review before any manual approval.");
        } else if (request.riskScore() >= 50) {
            actions.add("Review the triggered rule evidence and transfer history.");
        } else {
            actions.add("Confirm no additional velocity or account anomalies are present.");
        }
        actions.add("Record the analyst rationale before resolving the case.");
        return List.copyOf(actions);
    }
}
