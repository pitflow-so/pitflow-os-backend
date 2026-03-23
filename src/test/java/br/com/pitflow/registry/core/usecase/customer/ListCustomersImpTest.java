package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListCustomersImpTest {

    @Test
    @DisplayName("Should list all customers")
    void shouldListAllCustomers() {
        var repository = mock(CustomerGateway.class);
        var useCase = new ListCustomersImp(repository);
        when(repository.findAll()).thenReturn(List.of(mock(Customer.class), mock(Customer.class)));

        var result = useCase.execute();

        assertThat(result).hasSize(2);
        verify(repository).findAll();
    }
}