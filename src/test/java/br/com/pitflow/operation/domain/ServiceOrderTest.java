package br.com.pitflow.operation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ServiceOrderTest {
    @Test
    @DisplayName("Should create a service order with RECEIVED status and calculate amount correctly")
    void shouldCreateOrderAndCalculateTotal() {
        // Arrange
        var customerId = UUID.randomUUID();
        var vehicleId = UUID.randomUUID();
        var order = new ServiceOrder(customerId, vehicleId);

        var partId = UUID.randomUUID();
        var serviceId = UUID.randomUUID();

        // Act
        order.addPart(partId, "Filtro de Ar", new BigDecimal("100.00"), 2); // 200.00
        order.addService(serviceId, "Troca de Filtro", new BigDecimal("50.00")); // 50.00

        // Assert
        assertThat(order.getStatus()).isEqualTo(ServiceOrder.Status.RECEIVED);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    @Test
    @DisplayName("Should transit status correctly and set timestamps")
    void shouldTransitStatusCorrectly() {
        // Arrange
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID());

        // Act
        order.startDiagnosis(); // RECEIVED -> IN_DIAGNOSIS
        assertThat(order.getStatus()).isEqualTo(ServiceOrder.Status.IN_DIAGNOSIS);

        order.addService(UUID.randomUUID(), "General Checkup", new BigDecimal("100.00"));

        order.completeDiagnosis(); // IN_DIAGNOSIS -> AWAITING_APPROVAL
        assertThat(order.getStatus()).isEqualTo(ServiceOrder.Status.AWAITING_APPROVAL);

        order.approve(); // AWAITING_APPROVAL -> IN_EXECUTION

        // Assert
        assertThat(order.getStatus()).isEqualTo(ServiceOrder.Status.IN_EXECUTION);
        assertThat(order.getStartedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should cancel order successfully if not finished")
    void shouldCancelOrder() {
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID());

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(ServiceOrder.Status.CANCELLED);
    }

    @Test
    @DisplayName("Should throw exception when adding items after diagnosis stage")
    void shouldThrowExceptionWhenAddingItemsInWrongStage() {
        // Arrange
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID());
        order.startDiagnosis();
        order.addService(UUID.randomUUID(), "Service", new BigDecimal("100.00"));
        order.completeDiagnosis();

        assertThatThrownBy(() -> order.addService(UUID.randomUUID(), "Sneaky Service", new BigDecimal("10.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Items can only be added during RECEIVED or IN_DIAGNOSIS stages.");
    }

    @Test
    @DisplayName("Should prevent cancellation of finished orders")
    void shouldPreventCancellationOfFinishedOrders() {
        // Arrange
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID());
        order.startDiagnosis();
        order.addService(UUID.randomUUID(), "Service", new BigDecimal("100.00"));
        order.completeDiagnosis();
        order.approve();
        order.finish();

        // Act & Assert
        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel an order that is already finished or delivered.");
    }

    @Test
    @DisplayName("Should prevent cancellation of delivered Orders")
    void shouldPreventCancellationOfDeliveredOrders() {
        // Arrange
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID());
        order.startDiagnosis();
        order.addService(UUID.randomUUID(), "Service", new BigDecimal("100.00"));
        order.completeDiagnosis();
        order.approve();
        order.finish();
        order.deliver();

        // Act & Assert
        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel an order that is already finished or delivered.");
    }


}
