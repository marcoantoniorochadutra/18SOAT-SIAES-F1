package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateMaintenanceAppService implements CreateMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    @Transactional
    public MaintenanceId execute(CreateMaintenanceCommand command) {
        Maintenance maintenance = Maintenance.register(command.description(), command.price());
        return this.maintenanceRepository.save(maintenance).getId();
    }
}
