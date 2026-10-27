package com.fiap.siaes.workorder.infrastructure.persistence.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderJpaEntity;
import com.fiap.siaes.workorder.infrastructure.persistence.projection.ListWorkOrderProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderJpaEntity, WorkOrderId> {

    @Query(value = """
            SELECT
                w.id AS id,
                w.customer_id AS customerId,
                c.name AS customerName,
                w.vehicle_id AS vehicleId,
                v.plate AS vehiclePlate,
                v.brand AS vehicleBrand,
                v.model AS vehicleModel,
                w.last_status AS status,
                w.created_at AS createdAt,
                w.finished_at AS finishedAt
            FROM {h-schema} work_orders w
            INNER JOIN {h-schema} customers c ON c.id = w.customer_id
            INNER JOIN {h-schema} vehicles v ON v.id = w.vehicle_id
            WHERE (:customerId IS NULL OR w.customer_id = :customerId)
                AND (:document IS NULL OR c.document = :document)
                AND (:vehicleId IS NULL OR w.vehicle_id = :vehicleId)
                AND (:status IS NULL OR w.last_status = :status)
            ORDER BY w.created_at DESC
            """,
           nativeQuery = true)
    Slice<ListWorkOrderProjection> findAllWorkOrders(@Param("customerId") CustomerId customerId,
                                                     @Param("document") String document,
                                                     @Param("vehicleId") VehicleId vehicleId,
                                                     @Param("status") String status,
                                                     Pageable pageable);
}
