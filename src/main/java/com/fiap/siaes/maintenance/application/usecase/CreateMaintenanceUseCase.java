package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.maintenance.domain.model.MaintenanceId;

import java.math.BigDecimal;

public interface CreateMaintenanceUseCase {

    MaintenanceId execute(CreateMaintenanceCommand command);

    record CreateMaintenanceCommand(String description, BigDecimal price) {}
}
