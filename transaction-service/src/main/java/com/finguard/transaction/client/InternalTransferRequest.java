package com.finguard.transaction.client;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalTransferRequest(
        UUID transactionId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount) {
}
