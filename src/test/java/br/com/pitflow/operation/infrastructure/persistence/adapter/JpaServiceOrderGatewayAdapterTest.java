package br.com.pitflow.operation.infrastructure.persistence.adapter;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.infrastructure.persistence.entity.ServiceOrderJpa;
import br.com.pitflow.operation.infrastructure.persistence.mapper.ServiceOrderMapper;
import br.com.pitflow.operation.infrastructure.persistence.repository.SpringServiceOrderRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JpaServiceOrderGatewayAdapterTest {
    @Test
    void delegatesAndMapsEveryRepositoryOperation() {
        var repository = mock(SpringServiceOrderRepository.class);
        var adapter = new JpaServiceOrderGatewayAdapter(repository);
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "OS");
        ServiceOrderJpa entity = ServiceOrderMapper.toJpa(order);
        when(repository.findById(order.getId())).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(entity));
        when(repository.findByCustomerId(order.getCustomerId())).thenReturn(List.of(entity));
        when(repository.findByVehicleId(order.getVehicleId())).thenReturn(List.of(entity));
        when(repository.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.RECEIVED)).thenReturn(List.of(entity));
        when(repository.getAverageExecutionTimeInSeconds()).thenReturn(30.0);

        adapter.save(order);
        assertTrue(adapter.findById(order.getId()).isPresent());
        assertEquals(1, adapter.findAll().size());
        assertEquals(1, adapter.findAllByOrderByCreatedAtAsc().size());
        assertEquals(1, adapter.findByCustomerId(order.getCustomerId()).size());
        assertEquals(1, adapter.findByVehicleId(order.getVehicleId()).size());
        assertEquals(1, adapter.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.RECEIVED).size());
        assertEquals(30.0, adapter.getAverageExecutionTimeInSeconds());
        verify(repository).save(any());
    }
}
