package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.MaintenanceId;

import java.math.BigDecimal;

public interface UpdateMaintenanceUseCase {

    Maintenance execute(MaintenanceId id, UpdateMaintenanceCommand command);

    record UpdateMaintenanceCommand(String description, BigDecimal price) {}
}
