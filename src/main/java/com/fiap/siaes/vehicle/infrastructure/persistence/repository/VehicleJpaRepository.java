package com.fiap.siaes.vehicle.infrastructure.persistence.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fiap.siaes.vehicle.infrastructure.persistence.projection.ListVehicleProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleJpaRepository extends JpaRepository<VehicleJpaEntity, VehicleId> {

    List<VehicleJpaEntity> findByCustomerId(CustomerId customerId);

    boolean existsByPlate(String plate);

    boolean existsByPlateAndIdNot(String plate, VehicleId id);

    @Query(value = """
            SELECT
                v.id AS id,
                v.plate AS plate,
                v.model AS model,
                v.year AS year,
                v.customer_id AS customerId,
                c.name AS customerName
            FROM {h-schema} vehicles v
            INNER JOIN {h-schema} customers c ON c.id = v.customer_id
            WHERE (:plate IS NULL OR v.plate ILIKE '%' || :plate || '%')
                AND (:brand IS NULL OR v.brand ILIKE '%' || :brand || '%')
                AND (:model IS NULL OR v.model ILIKE '%' || :model || '%')
                AND (:year IS NULL OR v.year = :year)
                AND (:customerId IS NULL OR v.customer_id = :customerId)
            ORDER BY v.id
            """,
           nativeQuery = true)
    Slice<ListVehicleProjection> findAllVehicles(@Param("plate") String plate,
                                                 @Param("brand") String brand,
                                                 @Param("model") String model,
                                                 @Param("year") Integer year,
                                                 @Param("customerId") CustomerId customerId,
                                                 Pageable pageable);
}
