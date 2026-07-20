package com.finguard.transaction.service;

import com.finguard.events.FraudDecisionEvent;
import com.finguard.events.FraudDecisionType;
import com.finguard.events.TransactionCompletedEvent;
import com.finguard.events.TransactionFailedEvent;
import com.finguard.transaction.client.AccountClient;
import com.finguard.transaction.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.UUID;

@Service
public class TransferProcessor {
    public static final String COMPLETED_TOPIC = "transaction.completed.v1";
    public static final String FAILED_TOPIC = "transaction.failed.v1";

    private final BankTransactionRepository transactions;
    private final OutboxRepository outbox;
    private final AccountClient accounts;
    private final TransferService serializer;

    public TransferProcessor(BankTransactionRepository transactions, OutboxRepository outbox,
                             AccountClient accounts, TransferService serializer) {
        this.transactions = transactions;
        this.outbox = outbox;
        this.accounts = accounts;
        this.serializer = serializer;
    }

    @Transactional
    public void handle(FraudDecisionEvent decision) {
        BankTransaction transaction = transactions.findByIdForUpdate(decision.transactionId()).orElse(null);
        if (transaction == null || isTerminal(transaction.getStatus())) {
            return;
        }

        if (decision.decision() == FraudDecisionType.FLAGGED) {
            transaction.status(TransactionStatus.FLAGGED, String.join("; ", decision.reasons()));
            transactions.save(transaction);
            return;
        }
        if (decision.decision() == FraudDecisionType.REJECTED) {
            transaction.status(TransactionStatus.REJECTED, String.join("; ", decision.reasons()));
            transactions.save(transaction);
            return;
        }

        transaction.status(TransactionStatus.PROCESSING, null);
        transactions.save(transaction);

        try {
            accounts.transfer(transaction.getId(), transaction.getSourceAccountId(),
                    transaction.getDestinationAccountId(), transaction.getAmount());
            complete(transaction);
        } catch (HttpClientErrorException businessFailure) {
            fail(transaction, "Ledger rejected transfer: " + businessFailure.getStatusCode());
        } catch (RuntimeException retryableFailure) {
            /*
             * Do not mark an uncertain network or server failure as terminal. Throwing rolls
             * back this local transaction and lets Kafka redeliver. If the account service
             * committed before the response was lost, its transaction-id idempotency makes
             * the retry safe and returns the already-posted balances.
             */
            throw retryableFailure;
        }
    }

    private void complete(BankTransaction transaction) {
        transaction.status(TransactionStatus.COMPLETED, null);
        transactions.save(transaction);
        TransactionCompletedEvent event = new TransactionCompletedEvent(
                UUID.randomUUID(), transaction.getId(), transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(), transaction.getAmount(),
                transaction.getCurrency(), Instant.now());
        outbox.save(new OutboxEvent(transaction.getId(), COMPLETED_TOPIC, serializer.json(event)));
    }

    private void fail(BankTransaction transaction, String reason) {
        transaction.status(TransactionStatus.FAILED, reason);
        transactions.save(transaction);
        TransactionFailedEvent event = new TransactionFailedEvent(
                UUID.randomUUID(), transaction.getId(), reason, Instant.now());
        outbox.save(new OutboxEvent(transaction.getId(), FAILED_TOPIC, serializer.json(event)));
    }

    private boolean isTerminal(TransactionStatus status) {
        return status == TransactionStatus.COMPLETED
                || status == TransactionStatus.REJECTED
                || status == TransactionStatus.FAILED;
    }
}
