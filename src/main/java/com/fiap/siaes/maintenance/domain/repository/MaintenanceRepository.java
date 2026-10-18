package com.fiap.siaes.maintenance.domain.repository;

import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceNotFoundException;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;

public interface MaintenanceRepository extends RepositoryBase<Maintenance, MaintenanceId> {

    default Maintenance findByIdOrThrowNotFound(MaintenanceId id) {
        return this.findById(id)
                .orElseThrow(() -> new MaintenanceNotFoundException(id.toString()));
    }

    boolean existsByCode(MaintenanceCode code);

    boolean existsByCodeAndIdNot(MaintenanceCode code, MaintenanceId id);
}
