package com.finguard.transaction.api;
import jakarta.validation.constraints.*;import java.math.BigDecimal;import java.util.UUID;
public record TransferRequest(@NotNull UUID sourceAccountId,@NotNull UUID destinationAccountId,@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2) BigDecimal amount) {}
