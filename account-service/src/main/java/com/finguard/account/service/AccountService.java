package com.finguard.account.service;

import com.finguard.account.api.*;
import com.finguard.account.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accounts;
    private final AccountOperationRepository operations;
    private final SecureRandom random = new SecureRandom();

    public AccountService(AccountRepository accounts, AccountOperationRepository operations) {
        this.accounts = accounts;
        this.operations = operations;
    }

    public List<AccountResponse> ownedBy(String ownerId) {
        return accounts.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream().map(this::toResponse).toList();
    }

    public List<AccountResponse> all() {
        return accounts.findAll().stream().map(this::toResponse).toList();
    }

    public AccountResponse getOwned(UUID id, String ownerId, boolean admin) {
        Account account = find(id);
        if (!admin && !account.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account does not belong to authenticated user");
        }
        return toResponse(account);
    }

    public AccountResponse create(String ownerId, CreateAccountRequest request) {
        String number = "9" + String.format("%011d", Math.floorMod(random.nextLong(), 100_000_000_000L));
        return toResponse(accounts.save(new Account(
                UUID.randomUUID(), ownerId, number, java.math.BigDecimal.ZERO, request.currency())));
    }

    public AccountSnapshot snapshot(UUID id) {
        return snapshot(find(id));
    }

    /**
     * Posts both ledger legs inside the account service's single database transaction.
     * That is safer than a remote debit followed by a remote credit because an uncertain
     * HTTP response cannot leave the two balances in different states.
     */
    @Transactional
    public InternalTransferResponse transfer(InternalTransferRequest request) {
        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Source and destination accounts must differ");
        }

        // Lock in deterministic UUID order to reduce deadlock risk for opposing transfers.
        UUID firstId = request.sourceAccountId().compareTo(request.destinationAccountId()) < 0
                ? request.sourceAccountId() : request.destinationAccountId();
        UUID secondId = firstId.equals(request.sourceAccountId())
                ? request.destinationAccountId() : request.sourceAccountId();

        Account first = lock(firstId);
        Account second = lock(secondId);
        Account source = first.getId().equals(request.sourceAccountId()) ? first : second;
        Account destination = source == first ? second : first;

        boolean debitExists = operations.existsByAccountIdAndTransactionIdAndOperationType(
                source.getId(), request.transactionId(), OperationType.DEBIT);
        boolean creditExists = operations.existsByAccountIdAndTransactionIdAndOperationType(
                destination.getId(), request.transactionId(), OperationType.CREDIT);

        if (debitExists || creditExists) {
            if (!(debitExists && creditExists)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Incomplete ledger posting detected; manual reconciliation is required");
            }
            return response(request.transactionId(), source, destination, true);
        }

        if (!source.getCurrency().equals(destination.getCurrency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cross-currency transfers are not supported");
        }
        if (source.getBalance().compareTo(request.amount()) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient funds");
        }

        source.debit(request.amount());
        destination.credit(request.amount());
        accounts.save(source);
        accounts.save(destination);
        operations.save(new AccountOperation(source.getId(), request.transactionId(),
                OperationType.DEBIT, request.amount(), source.getBalance()));
        operations.save(new AccountOperation(destination.getId(), request.transactionId(),
                OperationType.CREDIT, request.amount(), destination.getBalance()));

        return response(request.transactionId(), source, destination, false);
    }

    private Account lock(UUID id) {
        return accounts.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    private InternalTransferResponse response(UUID transactionId, Account source,
                                              Account destination, boolean duplicate) {
        return new InternalTransferResponse(transactionId, source.getId(), source.getBalance(),
                destination.getId(), destination.getBalance(), source.getCurrency(), duplicate);
    }

    private AccountSnapshot snapshot(Account account) {
        return new AccountSnapshot(account.getId(), account.getOwnerId(),
                account.getBalance(), account.getCurrency());
    }

    private Account find(UUID id) {
        return accounts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId(), account.getOwnerId(), mask(account.getAccountNumber()),
                account.getBalance(), account.getCurrency(), account.getCreatedAt());
    }

    private String mask(String number) {
        return "•••• " + number.substring(number.length() - 4);
    }
}
