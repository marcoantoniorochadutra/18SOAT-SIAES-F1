package com.fiap.siaes.maintenance.infrastructure.persistence.mapper;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;

public final class MaintenanceMapper {

    private MaintenanceMapper() {
    }

    public static MaintenanceJpaEntity toEntity(Maintenance maintenance) {
        if (maintenance == null) {
            return null;
        }
        return new MaintenanceJpaEntity(maintenance.getId(), maintenance.getDescription(), maintenance.getPrice());
    }

    public static Maintenance toDomain(MaintenanceJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Maintenance.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .build();
    }
}
