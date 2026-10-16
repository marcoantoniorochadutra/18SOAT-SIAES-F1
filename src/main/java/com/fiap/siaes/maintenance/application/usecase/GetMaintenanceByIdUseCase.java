package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;

public interface GetMaintenanceByIdUseCase {

    Maintenance execute(MaintenanceId id);
}
