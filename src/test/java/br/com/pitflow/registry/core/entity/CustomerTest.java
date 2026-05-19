package br.com.pitflow.registry.core.entity;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.valueObject.Email;
import br.com.pitflow.registry.core.valueObject.LicensePlate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomerTest {

    @Test
    @DisplayName("Should create a customer")
    void shouldCreateCustomer() {
        // Arrange
        var document = new CpfCnpj("066.784.770-73");
        var email = new Email("joao@gmail.com");

        //Act
        var customer = new Customer("Jão Santos", "11999999999", email, document);
        customer.setId(UUID.randomUUID());

        // Assert
        assertThat(customer.getName()).isEqualTo("Jão Santos");
        assertThat(customer.getDocument().value()).isEqualTo("06678477073");
        assertThat(customer.getEmail().value()).isEqualTo("joao@gmail.com");
        assertThat(customer.getPhone()).isEqualTo("11999999999");
    }

    @Test
    @DisplayName("Should throw exception when creating customer with empty name")
    void shouldThrowExceptionForEmptyName() {
        var document = new CpfCnpj("066.784.770-73");
        var email = new Email("dummy@gmail.com");

        assertThatThrownBy(() -> new Customer("", "11999999999", email, document))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer name cannot be empty.");
    }
}
