package com.finguard.transaction.api;
import com.finguard.transaction.domain.TransactionStatus;import java.math.BigDecimal;import java.time.Instant;import java.util.UUID;
public record TransactionResponse(UUID id,UUID sourceAccountId,UUID destinationAccountId,BigDecimal amount,String currency,TransactionStatus status,String failureReason,Instant createdAt,Instant updatedAt) {}
