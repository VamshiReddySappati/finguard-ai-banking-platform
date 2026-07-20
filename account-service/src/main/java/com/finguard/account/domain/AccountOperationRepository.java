package com.finguard.account.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AccountOperationRepository extends JpaRepository<AccountOperation,UUID> {
    boolean existsByAccountIdAndTransactionIdAndOperationType(UUID accountId,UUID transactionId,OperationType type);
}
