package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase;
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
public class CreateMaintenanceAppService implements CreateMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MaintenanceId execute(CreateMaintenanceCommand command) {
        Maintenance maintenance = this.createMaintenance(command);

        this.validateMaintenance(maintenance);

        this.maintenanceRepository.save(maintenance);

        return maintenance.getId();
    }

    private void validateMaintenance(Maintenance maintenance) {
        if (this.maintenanceRepository.existsByCode(maintenance.getCode())) {
            throw new MaintenanceCodeAlreadyExistsException(maintenance.getCode().getValue());
        }
    }

    private Maintenance createMaintenance(CreateMaintenanceCommand command) {
        return Maintenance.builder()
                .code(command.code())
                .description(command.description())
                .price(command.price())
                .estimatedDurationMinutes(command.estimatedDurationMinutes())
                .build();
    }
}
