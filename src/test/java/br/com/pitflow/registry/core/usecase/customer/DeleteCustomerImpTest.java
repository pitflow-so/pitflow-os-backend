package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteCustomerImpTest {
    private CustomerGateway gateway;
    private DeleteCustomerImp deleteCustomer;

    @BeforeEach
    void setUp() {
        gateway = mock(CustomerGateway.class);
        deleteCustomer = new DeleteCustomerImp(gateway);
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.of(mock(Customer.class)));

        // Act
        deleteCustomer.execute(id);

        // Verify
        verify(gateway).delete(id);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteCustomer.execute(id))
                .isInstanceOf(IllegalArgumentException.class);

        // Verify
        verify(gateway, never()).delete(any());
    }
}