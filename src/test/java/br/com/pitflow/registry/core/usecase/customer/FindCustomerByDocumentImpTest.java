package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FindCustomerByDocumentImpTest {

    private CustomerGateway gateway;
    private FindCustomerByDocumentImp findCustomerByDocument;

    @BeforeEach
    void setUp() {
        gateway = mock(CustomerGateway.class);
        findCustomerByDocument = new FindCustomerByDocumentImp(gateway);
    }

    @Test
    @DisplayName("Should return customer when document exists")
    void shouldReturnCustomerWhenDocumentExists() {
        // Arrange
        String docValue = "42634554010";
        var cpfCnpj = new CpfCnpj(docValue);
        var customer = new Customer("João Silva", cpfCnpj, "11999999999");

        when(gateway.findByDocument(any(CpfCnpj.class))).thenReturn(Optional.of(customer));

        // Act
        Customer result = findCustomerByDocument.execute(docValue);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDocument().value()).isEqualTo(docValue);
        assertThat(result.getName()).isEqualTo("João Silva");
        verify(gateway, times(1)).findByDocument(any(CpfCnpj.class));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        String docValue = "42634554010";
        when(gateway.findByDocument(any(CpfCnpj.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findCustomerByDocument.execute(docValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found with document: " + docValue);

        verify(gateway, times(1)).findByDocument(any(CpfCnpj.class));
    }
}
