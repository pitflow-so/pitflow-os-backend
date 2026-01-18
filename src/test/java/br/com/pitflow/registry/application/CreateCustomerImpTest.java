package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.aplication.CreateCustomerImp;
import br.com.pitflow.registry.aplication.dto.CreateCustomerDto;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class CreateCustomerImpTest {

    private CustomerRepository repository;
    private CreateCustomerImp createCustomer;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);
        createCustomer = new CreateCustomerImp(repository);
    }

    @Test
    @DisplayName("Should create customer with success")
    void shouldCreateCustomerWithSuccess() {
        // Arrange
        var dto = new CreateCustomerDto("Rafael Moreira", "06678477073", "11996195936");
        when(repository.findByDocument(any(CpfCnpj.class))).thenReturn(Optional.empty());

        // Act
        Customer result = createCustomer.execute(dto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Rafael Moreira", result.getName());
        assertEquals("06678477073", result.getDocument().value());

        // verify
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should Throw Exception When Document Already Exists")
    void shouldThrowExceptionWhenDocumentAlreadyExists() {
        // Arrange
        var dto = new CreateCustomerDto("Rafael Moreira", "06678477073", "11996195936");
        var existingCustomer = new Customer("Outro Nome", new CpfCnpj("06678477073"), "11000000000");
        var exceptionMessage = String.format("Customer with document %s already exists.", existingCustomer.getDocument().value());

        when(repository.findByDocument(any(CpfCnpj.class))).thenReturn(Optional.of(existingCustomer));

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> createCustomer.execute(dto));
        assertEquals(exceptionMessage, exception.getMessage());

        // verify
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should Throw Exception When Document Is Invalid")
    void shouldThrowExceptionWhenDocumentIsInvalid() {
        // Arrange
        var dto = new CreateCustomerDto("Rafael Moreira", "123", "11996195936");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createCustomer.execute(dto));

        // verify
        verifyNoInteractions(repository);
    }
}
