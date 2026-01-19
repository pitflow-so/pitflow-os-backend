package br.com.pitflow.operation.application;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApproveOrderImpTest {

    private ServiceOrderRepository repository;
    private ApproveOrderImp approveOrder;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        approveOrder = new ApproveOrderImp(repository);
    }

    @Test
    @DisplayName("Should approve order and start execution timestamp successfully")
    void shouldApproveOrderSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Barulho na suspensão");
        os.startDiagnosis();
        os.addService(UUID.randomUUID(), "Reparo", new BigDecimal("150.0"));
        os.completeDiagnosis(); // Agora está AWAITING_APPROVAL

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act
        approveOrder.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.IN_EXECUTION);
        assertThat(os.getExecutionStartedAt()).isNotNull(); // Essencial para o Requisito 40
        verify(repository).save(os);
    }

    @Test
    @DisplayName("Should fail to approve if order is not in AWAITING_APPROVAL state")
    void shouldFailIfInvalidState() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Troca de óleo");

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() -> approveOrder.execute(osId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order must be AWAITING_APPROVAL to be approved.");
    }
}