package com.finguard.investigation.api;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record InvestigationRequest(
        @NotNull UUID transactionId,
        @NotNull @DecimalMin("0.00") BigDecimal amount,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
        @NotBlank @Size(max = 40) String status,
        @Min(0) @Max(100) int riskScore,
        @NotNull @Size(max = 20) List<@Size(max = 300) String> reasons,
        @NotNull @Size(max = 30) List<@Size(max = 100) String> eventTypes) {
}
