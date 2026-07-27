package br.com.pitflow.operation.infrastructure.outbox;

import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JpaOperationEventGatewayAdapterTest {

    @Test
    void serializesOccurredAtAsContractualIsoDateTime() {
        var repository = mock(SpringOutboxRepository.class);
        var adapter = new JpaOperationEventGatewayAdapter(
                repository,
                JsonMapper.builder().findAndAddModules().build()
        );
        var occurredAt = Instant.parse("2026-07-27T03:44:01.538818800Z");

        adapter.saveBudgetApproved(new ServiceOrderBudgetApproved(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("250.99"),
                occurredAt
        ));

        var captor = ArgumentCaptor.forClass(OutboxMessageJpa.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPayload())
                .contains("\"occurredAt\":\"2026-07-27T03:44:01.538818800Z\"");
    }
}
