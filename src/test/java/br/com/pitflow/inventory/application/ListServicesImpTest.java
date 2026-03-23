package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.inventory.core.usecase.service.ListServicesImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListServicesImpTest {

    private ServiceGateway repository;
    private ListServicesImp listServices;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceGateway.class);
        listServices = new ListServicesImp(repository);
    }

    @Test
    @DisplayName("Should return all services")
    void shouldReturnAllServices() {
        // Arrange
        var service1 = new Service("Alinhamento", "Alinhamento de direção", new BigDecimal("80.00"));
        var service2 = new Service("Balanceamento", "Balanceamento de rodas", new BigDecimal("60.00"));
        var mockList = List.of(service1, service2);

        when(repository.findAll()).thenReturn(mockList);

        // Act
        List<Service> result = listServices.execute();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(service1, service2);

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no services exist")
    void shouldReturnEmptyListWhenNoServicesExist() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Service> result = listServices.execute();

        // Assert
        assertThat(result).isEmpty();
        verify(repository, times(1)).findAll();
    }
}