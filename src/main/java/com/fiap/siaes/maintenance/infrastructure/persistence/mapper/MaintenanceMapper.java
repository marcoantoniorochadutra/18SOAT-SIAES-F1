package com.fiap.siaes.maintenance.infrastructure.persistence.mapper;

import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;
import com.fiap.siaes.maintenance.infrastructure.persistence.projection.ListMaintenanceProjection;
import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class MaintenanceMapper {

    public static MaintenanceJpaEntity toEntity(Maintenance maintenance) {
        if (isNull(maintenance))
            return null;

        return MaintenanceJpaEntity.builder()
                .id(maintenance.getId())
                .code(maintenance.getCode().getValue())
                .description(maintenance.getDescription())
                .price(maintenance.getPrice())
                .estimatedDurationMinutes(maintenance.getEstimatedDurationMinutes())
                .active(maintenance.isActive())
                .build();
    }

    public static Maintenance toDomain(MaintenanceJpaEntity entity) {
        if (isNull(entity))
            return null;

        return Maintenance.recreate()
                .id(entity.getId())
                .code(MaintenanceCode.recreate(entity.getCode()))
                .description(entity.getDescription())
                .price(entity.getPrice())
                .estimatedDurationMinutes(entity.getEstimatedDurationMinutes())
                .active(entity.isActive())
                .build();
    }

    public static MaintenanceResponse toResponse(ListMaintenanceProjection projection) {
        return new MaintenanceResponse(
                MaintenanceId.from(projection.getId()),
                projection.getCode(),
                projection.getDescription(),
                projection.getPrice(),
                projection.getEstimatedDurationMinutes(),
                projection.isActive());
    }
}
