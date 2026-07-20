package com.finguard.account.api;
import java.math.BigDecimal;
import java.util.UUID;
public record AccountSnapshot(UUID id,String ownerId,BigDecimal balance,String currency) {}
