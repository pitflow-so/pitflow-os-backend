package br.com.pitflow.operation.infrastructure.persistence.repository;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.infrastructure.persistence.entity.ServiceOrderJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SpringServiceOrderRepository extends JpaRepository<ServiceOrderJpa, UUID> {
    List<ServiceOrderJpa> findByCustomerId(UUID customerId);

    List<ServiceOrderJpa> findByVehicleId(UUID vehicleId);

    List<ServiceOrderJpa> findAllByOrderByCreatedAtAsc();

    List<ServiceOrderJpa> findByStatusOrderByCreatedAtAsc(ServiceOrder.Status status);

    // Calcula a média em segundos entre o início da execução e a finalização
    // Filtrado por ordens que já foram FINALIZADAS (FINISHED ou DELIVERED)
    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (finished_at - execution_started_at))) " +
            "FROM service_orders " +
            "WHERE status IN ('FINISHED', 'DELIVERED') " +
            "AND execution_started_at IS NOT NULL " +
            "AND finished_at IS NOT NULL", nativeQuery = true)
    Double getAverageExecutionTimeInSeconds();
}
