package com.finguard.transaction.domain;
import jakarta.persistence.LockModeType;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import java.util.*;
public interface BankTransactionRepository extends JpaRepository<BankTransaction,UUID> {
 Optional<BankTransaction> findByInitiatedByAndIdempotencyKey(String initiatedBy,String idempotencyKey);
 List<BankTransaction> findByInitiatedByOrderByCreatedAtDesc(String initiatedBy);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from BankTransaction t where t.id=:id") Optional<BankTransaction> findByIdForUpdate(@Param("id") UUID id);
}
