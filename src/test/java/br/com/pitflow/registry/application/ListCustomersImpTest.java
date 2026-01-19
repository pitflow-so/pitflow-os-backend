package br.com.pitflow.registry.application;

import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListCustomersImpTest {

    @Test
    @DisplayName("Should list all customers")
    void shouldListAllCustomers() {
        var repository = mock(CustomerRepository.class);
        var useCase = new ListCustomersImp(repository);
        when(repository.findAll()).thenReturn(List.of(mock(Customer.class), mock(Customer.class)));

        var result = useCase.execute();

        assertThat(result).hasSize(2);
        verify(repository).findAll();
    }
}