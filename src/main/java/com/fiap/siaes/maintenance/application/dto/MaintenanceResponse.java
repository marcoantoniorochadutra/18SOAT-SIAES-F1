package com.fiap.siaes.maintenance.application.dto;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;

import java.math.BigDecimal;

public record MaintenanceResponse(MaintenanceId id, String code, String description, BigDecimal price,
                                  int estimatedDurationMinutes, boolean active) {

    public static MaintenanceResponse from(Maintenance maintenance) {
        return new MaintenanceResponse(
                maintenance.getId(),
                maintenance.getCode().getValue(),
                maintenance.getDescription(),
                maintenance.getPrice(),
                maintenance.getEstimatedDurationMinutes(),
                maintenance.isActive());
    }
}
