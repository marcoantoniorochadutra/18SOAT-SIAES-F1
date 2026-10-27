package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeAlreadyExistsException;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMaintenanceAppService implements UpdateMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MaintenanceId execute(UpdateMaintenanceCommand command) {
        Maintenance maintenance = this.maintenanceRepository.findByIdOrThrowNotFound(command.id());

        maintenance.update(command.code(), command.description(), command.price(), command.estimatedDurationMinutes());

        this.validateMaintenance(maintenance);

        this.maintenanceRepository.save(maintenance);

        return maintenance.getId();
    }

    private void validateMaintenance(Maintenance maintenance) {
        if (this.maintenanceRepository.existsByCodeAndIdNot(maintenance.getCode(), maintenance.getId())) {
            throw new MaintenanceCodeAlreadyExistsException(maintenance.getCode().getValue());
        }
    }
}
