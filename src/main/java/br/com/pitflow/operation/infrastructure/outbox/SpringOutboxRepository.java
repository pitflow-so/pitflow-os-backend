package br.com.pitflow.operation.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringOutboxRepository
        extends JpaRepository<OutboxMessageJpa, UUID> {

    @Query(
            value = """
                    SELECT *
                    FROM operation_outbox
                    WHERE (
                        status = 'PENDING'
                        AND available_at <= CURRENT_TIMESTAMP
                    ) OR (
                        status = 'PROCESSING'
                        AND locked_until < CURRENT_TIMESTAMP
                    )
                    ORDER BY created_at
                    LIMIT :batchSize
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxMessageJpa> findClaimableForUpdate(
            @Param("batchSize") int batchSize
    );
}
