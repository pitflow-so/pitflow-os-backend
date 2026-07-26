package br.com.pitflow.operation.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringOutboxRepository
        extends JpaRepository<OutboxMessageJpa, UUID> {
}
