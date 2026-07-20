package com.finguard.account.service;

import com.finguard.account.api.InternalTransferRequest;
import com.finguard.account.domain.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    @Test
    void transferPostsBothLedgerLegsAtomically() {
        AccountRepository accounts = mock(AccountRepository.class);
        AccountOperationRepository operations = mock(AccountOperationRepository.class);
        Account source = new Account(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "user", "900000001234", new BigDecimal("100.00"), "USD");
        Account destination = new Account(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "other", "900000005678", new BigDecimal("20.00"), "USD");
        when(accounts.findByIdForUpdate(source.getId())).thenReturn(Optional.of(source));
        when(accounts.findByIdForUpdate(destination.getId())).thenReturn(Optional.of(destination));

        AccountService service = new AccountService(accounts, operations);
        var result = service.transfer(new InternalTransferRequest(
                UUID.randomUUID(), source.getId(), destination.getId(), new BigDecimal("25.00")));

        assertThat(result.sourceBalance()).isEqualByComparingTo("75.00");
        assertThat(result.destinationBalance()).isEqualByComparingTo("45.00");
        assertThat(result.duplicate()).isFalse();
        verify(operations, times(2)).save(any(AccountOperation.class));
    }
}
