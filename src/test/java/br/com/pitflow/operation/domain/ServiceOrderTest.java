package br.com.pitflow.operation.domain;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ServiceOrderTest {

    private final UUID customerId = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();
    private final String description = "Dummy problem";

    @Test
    @DisplayName("Should initialize correctly")
    void shouldInitializeCorrectly() {
        // Arrange & Act
        var os = new ServiceOrder(customerId, vehicleId, description);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.RECEIVED);
        assertThat(os.getCreatedAt()).isBeforeOrEqualTo(java.time.LocalDateTime.now());
        assertThat(os.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Should not create order if no description")
    void shouldNotCreateOrderIfNoProblemDescription(){
        assertThatThrownBy(() -> new ServiceOrder(customerId, vehicleId, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service order description cannot be empty.");
    }

    @Nested
    @DisplayName("Testes de Itens e Orçamento")
    class ItemTests {
        @Test
        @DisplayName("Should add parts and services correctly")
        void shouldCalculateTotalAmount() {
            // Arrange
            var os = new ServiceOrder(customerId, vehicleId, description);

            os.addPart(UUID.randomUUID(), "Óleo", new BigDecimal("50.00"), 4);
            os.addService(UUID.randomUUID(), "Troca de Óleo", new BigDecimal("100.00"));

            // Act & Assert
            assertThat(os.getTotalAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
        }

        @Test
        @DisplayName("Should fail to add item in invalid state")
        void shouldFailToAddItemInInvalidState() {
            // Arrange
            var os = new ServiceOrder(customerId, vehicleId, description);
            os.startDiagnosis();
            os.addPart(UUID.randomUUID(), "Peça", new BigDecimal("10.00"), 1);
            os.completeDiagnosis(); // Status agora é AWAITING_APPROVAL

            // Act & Assert
            assertThatThrownBy(() -> os.addPart(UUID.randomUUID(), "Outra", new BigDecimal("10.00"), 1))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Items can only be added during RECEIVED or IN_DIAGNOSIS stages.");
        }
    }

    @Nested
    @DisplayName("Testes de Transição de Status e Timestamps")
    class StatusTransitionTests {

        @Test
        @DisplayName("Should follow full lifecycle successfully")
        void shouldFollowFullLifecycleSuccessfully() {
            // Arrange
            var os = new ServiceOrder(customerId, vehicleId, description);

            // 1. Diagnosis
            os.startDiagnosis();
            assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.IN_DIAGNOSIS);
            assertThat(os.getDiagnosisStartedAt()).isNotNull();

            // 2. Add item and Complete Diagnosis
            os.addService(UUID.randomUUID(), "Revisão", new BigDecimal("200.00"));
            os.completeDiagnosis();
            assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.AWAITING_APPROVAL);

            // 3. Approve
            os.approve();
            assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.IN_EXECUTION);
            assertThat(os.getExecutionStartedAt()).isNotNull();

            // 4. Finish
            os.finish();
            assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.FINISHED);

            // 5. Deliver
            os.deliver();
            assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.DELIVERED);
        }

        @Test
        @DisplayName("Should not complete diagnosis without items added")
        void shouldFailToCompleteDiagnosisWithoutItems() {
            // Arrange
            var os = new ServiceOrder(customerId, vehicleId, description);
            os.startDiagnosis();

            // Act & Assert
            assertThatThrownBy(os::completeDiagnosis)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot complete diagnosis without adding at least one item.");
        }

        @Test
        @DisplayName("Should cancel successfully when in valid state")
        void shouldCancelSuccessfully() {
            // Arrange
            var os = new ServiceOrder(customerId, vehicleId, description);

            // Act
            os.cancel("Dummy reason");

            // Assert
            assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.CANCELLED);
        }

        @Test
        @DisplayName("Should not allow cancellation if already finished")
        void shouldNotCancelIfFinished() {
            // Arrange
            var os = new ServiceOrder(customerId, vehicleId, description);
            os.addService(UUID.randomUUID(), "S", new BigDecimal("10"));
            os.startDiagnosis();
            os.completeDiagnosis();
            os.approve();
            os.finish();

            // Act & Assert
            assertThatThrownBy(() -> os.cancel("Dummy reason"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot cancel an order that is already finished or delivered.");
        }

    }
}
