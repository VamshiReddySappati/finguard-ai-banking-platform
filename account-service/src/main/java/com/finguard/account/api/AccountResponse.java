package com.finguard.account.api;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record AccountResponse(UUID id,String ownerId,String accountNumber,BigDecimal balance,String currency,Instant createdAt) {}
