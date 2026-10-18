package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;

public interface GetMaintenanceByIdUseCase {

    MaintenanceResponse execute(MaintenanceId id);
}
