package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.maintenance.domain.model.MaintenanceId;

public interface DeleteMaintenanceUseCase {

    void execute(MaintenanceId id);
}
