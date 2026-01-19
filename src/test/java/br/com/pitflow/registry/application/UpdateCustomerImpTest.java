package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.application.dto.UpdateCustomerDto;
import br.com.pitflow.registry.application.usecases.UpdateCustomer;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateCustomerImpTest {
    private CustomerRepository repository;
    private UpdateCustomer updateCustomer;

    @BeforeEach void setUp() {
        repository = mock(CustomerRepository.class);
        updateCustomer = new UpdateCustomerImp(repository);
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        var customer = new Customer("Antigo", new CpfCnpj("12345678909"), "111");
        var dto = new UpdateCustomerDto("Novo", "98765432100", "222");

        when(repository.findById(id)).thenReturn(Optional.of(customer));
        when(repository.findByDocument(any())).thenReturn(Optional.empty());

        // Act
        updateCustomer.execute(id, dto);

        // Assert
        assertThat(customer.getName()).isEqualTo("Novo");

        // verify
        verify(repository).save(customer);
    }

    @Test
    @DisplayName("Should throw exception when document conflict")
    void shouldThrowExceptionWhenDocumentConflict() {
        // Arrange
        UUID id = UUID.randomUUID();
        var customer = new Customer("Cliente A", new CpfCnpj("27278293022"), "111");
        var another = new Customer("Cliente B", new CpfCnpj("42634554010"), "222");
        var dto = new UpdateCustomerDto("Cliente A", "42634554010", "111");

        when(repository.findById(id)).thenReturn(Optional.of(customer));
        when(repository.findByDocument(new CpfCnpj("42634554010"))).thenReturn(Optional.of(another));

        // Act & Assert
        assertThatThrownBy(() -> updateCustomer.execute(id, dto))
                .isInstanceOf(IllegalStateException.class);
    }
}