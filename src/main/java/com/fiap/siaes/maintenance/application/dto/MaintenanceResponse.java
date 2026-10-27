package com.fiap.siaes.maintenance.application.dto;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record MaintenanceResponse(MaintenanceId id, String code, String description, BigDecimal price,
                                  int estimatedDurationMinutes, boolean active) {

    public static MaintenanceResponse from(Maintenance maintenance) {
        return MaintenanceResponse.builder()
                .id(maintenance.getId())
                .code(maintenance.getCode().getValue())
                .description(maintenance.getDescription())
                .price(maintenance.getPrice())
                .estimatedDurationMinutes(maintenance.getEstimatedDurationMinutes())
                .active(maintenance.isActive())
                .build();
    }
}
