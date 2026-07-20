package com.finguard.account.api;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalTransferResponse(
        UUID transactionId,
        UUID sourceAccountId,
        BigDecimal sourceBalance,
        UUID destinationAccountId,
        BigDecimal destinationBalance,
        String currency,
        boolean duplicate) {
}
