package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;

public interface DeactivateMaintenanceUseCase {

    void execute(MaintenanceId id);
}
