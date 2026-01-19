package br.com.pitflow.operation.application;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllServiceOrdersImpTest {

    private ServiceOrderRepository repository;
    private FindAllServiceOrdersImp findAllServiceOrders;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        findAllServiceOrders = new FindAllServiceOrdersImp(repository);
    }

    @Test
    @DisplayName("Deve retornar uma lista com todas as ordens de serviço existentes")
    void shouldReturnListOfAllServiceOrders() {
        // Arrange
        var os1 = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Troca de freios");
        var os2 = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Alinhamento");
        when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(os1, os2));

        // Act
        var result = findAllServiceOrders.execute();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(os1, os2);

        // Verify
        verify(repository, times(1)).findAllByOrderByCreatedAtAsc();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver ordens de serviço")
    void shouldReturnEmptyListWhenNoOrdersExist() {
        // Arrange
        when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(Collections.emptyList());

        // Act
        var result = findAllServiceOrders.execute();

        // Assert
        assertThat(result).isEmpty();

        // Verify
        verify(repository, times(1)).findAllByOrderByCreatedAtAsc();
    }
}