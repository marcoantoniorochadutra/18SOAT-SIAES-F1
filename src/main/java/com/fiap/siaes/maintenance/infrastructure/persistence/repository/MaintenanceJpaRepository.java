package com.fiap.siaes.maintenance.infrastructure.persistence.repository;

import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;
import com.fiap.siaes.maintenance.infrastructure.persistence.projection.ListMaintenanceProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaintenanceJpaRepository extends JpaRepository<MaintenanceJpaEntity, MaintenanceId> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, MaintenanceId id);

    @Query(value = """
            SELECT
                m.id AS id,
                m.code AS code,
                m.description AS description,
                m.price AS price,
                m.estimated_duration_minutes AS estimatedDurationMinutes,
                m.active AS active
            FROM {h-schema} maintenances m
            WHERE (:code IS NULL OR m.code ILIKE '%' || :code || '%')
                AND (:description IS NULL OR m.description ILIKE '%' || :description || '%')
                AND m.active = :active
            ORDER BY m.description
            """,
           nativeQuery = true)
    Slice<ListMaintenanceProjection> findAllMaintenances(@Param("code") String code,
                                                         @Param("description") String description,
                                                         @Param("active") boolean active,
                                                         Pageable pageable);
}
