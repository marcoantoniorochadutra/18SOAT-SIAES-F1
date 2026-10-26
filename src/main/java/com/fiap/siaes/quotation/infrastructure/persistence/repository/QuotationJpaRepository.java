package com.fiap.siaes.quotation.infrastructure.persistence.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.projection.ListQuotationProjection;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface QuotationJpaRepository extends JpaRepository<QuotationJpaEntity, QuotationId> {

    boolean existsByWorkOrderIdAndLastStatusNotIn(WorkOrderId workOrderId, Collection<QuotationStatus> lastStatuses);

    boolean existsByWorkOrderIdAndLastStatus(WorkOrderId workOrderId, QuotationStatus lastStatus);

    Optional<QuotationJpaEntity> findByWorkOrderIdAndLastStatus(WorkOrderId workOrderId, QuotationStatus lastStatus);

    @Query(value = """
            SELECT
                q.id AS id,
                q.work_order_id AS workOrderId,
                q.customer_id AS customerId,
                c.name AS customerName,
                q.vehicle_id AS vehicleId,
                v.plate AS vehiclePlate,
                v.brand AS vehicleBrand,
                v.model AS vehicleModel,
                q.description AS description,
                q.last_status AS status,
                COALESCE((SELECT SUM(s.unit_price * s.quantity) FROM {h-schema} quotation_supplies s WHERE s.quotation_id = q.id), 0)
                    + COALESCE((SELECT SUM(m.unit_price * m.quantity) FROM {h-schema} quotation_maintenances m WHERE m.quotation_id = q.id), 0) AS totalAmount,
                q.created_at AS createdAt
            FROM {h-schema} quotations q
            INNER JOIN {h-schema} customers c ON c.id = q.customer_id
            INNER JOIN {h-schema} vehicles v ON v.id = q.vehicle_id
            WHERE (:workOrderId IS NULL OR q.work_order_id = :workOrderId)
                AND (:customerId IS NULL OR q.customer_id = :customerId)
                AND (:vehicleId IS NULL OR q.vehicle_id = :vehicleId)
                AND (:status IS NULL OR q.last_status = :status)
                AND (:includeDrafts OR q.last_status <> 'DRAFT')
            ORDER BY q.created_at DESC
            """,
           nativeQuery = true)
    Slice<ListQuotationProjection> findAllQuotations(@Param("workOrderId") WorkOrderId workOrderId,
                                                     @Param("customerId") CustomerId customerId,
                                                     @Param("vehicleId") VehicleId vehicleId,
                                                     @Param("status") String status,
                                                     @Param("includeDrafts") boolean includeDrafts,
                                                     Pageable pageable);
}
