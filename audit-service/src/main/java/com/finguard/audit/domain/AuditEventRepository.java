package com.finguard.audit.domain;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface AuditEventRepository extends JpaRepository<AuditEvent,UUID>{boolean existsBySourceEventId(UUID sourceEventId);List<AuditEvent> findByTransactionIdOrderByCreatedAtAsc(UUID transactionId);List<AuditEvent> findTop200ByOrderByCreatedAtDesc();}
