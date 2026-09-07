package com.fiap.siaes.maintenance.infrastructure.persistence.repository;

import com.fiap.siaes.maintenance.domain.model.MaintenanceId;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceJpaRepository extends JpaRepository<MaintenanceJpaEntity, MaintenanceId> {
}
