package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.valueObject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FindCustomerByIdImpTest {

    private CustomerGateway gateway;
    private FindCustomerByIdImp findCustomerById;

    @BeforeEach
    void setUp() {
        gateway = mock(CustomerGateway.class);
        findCustomerById = new FindCustomerByIdImp(gateway);
    }

    @Test
    @DisplayName("Should return customer when ID exists")
    void shouldReturnCustomerWhenIdExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        var customer = new Customer(
                "Fulano de Tal",
                "11988887777",
                new Email("fulano@gmail.com"),
                new CpfCnpj("42634554010")
        );
        customer.setId(id);

        when(gateway.findById(id)).thenReturn(Optional.of(customer));

        // Act
        Customer result = findCustomerById.execute(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Fulano de Tal");

        // Verify
        verify(gateway, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findCustomerById.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found with ID: " + id);

        // Verify
        verify(gateway, times(1)).findById(id);
    }
}