package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.controller.dto.UpdateCustomerCommand;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.UpdateCustomer;
import br.com.pitflow.registry.core.valueObject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateCustomerImpTest {
    private CustomerGateway gateway;
    private UpdateCustomer updateCustomer;

    @BeforeEach void setUp() {
        gateway = mock(CustomerGateway.class);
        updateCustomer = new UpdateCustomerImp(gateway);
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        var customer = new Customer(
                "Antigo",
                "111",
                new Email("dummy-older@gmail.com"),
                new CpfCnpj("12345678909")
        );
        var dto = new UpdateCustomerCommand("Novo", "98765432100", "222", "dummy-new@gmail.com");

        when(gateway.findById(id)).thenReturn(Optional.of(customer));
        when(gateway.findByDocument(any())).thenReturn(Optional.empty());

        // Act
        updateCustomer.execute(id, dto);

        // Assert
        assertThat(customer.getName()).isEqualTo("Novo");

        // verify
        verify(gateway).save(customer);
    }

    @Test
    @DisplayName("Should throw exception when document conflict")
    void shouldThrowExceptionWhenDocumentConflict() {
        // Arrange
        UUID id = UUID.randomUUID();
        var customer = new Customer("Cliente A", "111", new Email("a@gmail.com"), new CpfCnpj("27278293022"));
        var another = new Customer("Cliente B", "222", new Email("a@gmail.com"), new CpfCnpj("42634554010"));
        var dto = new UpdateCustomerCommand("Cliente A", "42634554010", "111", "a-new@gmail.com");

        when(gateway.findById(id)).thenReturn(Optional.of(customer));
        when(gateway.findByDocument(new CpfCnpj("42634554010"))).thenReturn(Optional.of(another));

        // Act & Assert
        assertThatThrownBy(() -> updateCustomer.execute(id, dto))
                .isInstanceOf(IllegalStateException.class);
    }
}