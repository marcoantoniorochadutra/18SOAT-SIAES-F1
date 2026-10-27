package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.DeactivateMaintenanceUseCase;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeactivateMaintenanceAppService implements DeactivateMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(MaintenanceId id) {
        Maintenance maintenance = this.maintenanceRepository.findByIdOrThrowNotFound(id);

        maintenance.deactivate();

        this.maintenanceRepository.save(maintenance);
    }
}
