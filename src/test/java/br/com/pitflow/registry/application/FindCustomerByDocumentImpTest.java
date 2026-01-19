package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FindCustomerByDocumentImpTest {

    private CustomerRepository repository;
    private FindCustomerByDocumentImp findCustomerByDocument;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);
        findCustomerByDocument = new FindCustomerByDocumentImp(repository);
    }

    @Test
    @DisplayName("Should return customer when document exists")
    void shouldReturnCustomerWhenDocumentExists() {
        // Arrange
        String docValue = "42634554010";
        var cpfCnpj = new CpfCnpj(docValue);
        var customer = new Customer("João Silva", cpfCnpj, "11999999999");

        when(repository.findByDocument(any(CpfCnpj.class))).thenReturn(Optional.of(customer));

        // Act
        Customer result = findCustomerByDocument.execute(docValue);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDocument().value()).isEqualTo(docValue);
        assertThat(result.getName()).isEqualTo("João Silva");
        verify(repository, times(1)).findByDocument(any(CpfCnpj.class));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        String docValue = "42634554010";
        when(repository.findByDocument(any(CpfCnpj.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findCustomerByDocument.execute(docValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found with document: " + docValue);

        verify(repository, times(1)).findByDocument(any(CpfCnpj.class));
    }
}
