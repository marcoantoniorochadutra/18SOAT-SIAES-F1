package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMaintenanceAppService implements UpdateMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    @Transactional
    public Maintenance execute(MaintenanceId id, UpdateMaintenanceCommand command) {
        Maintenance maintenance = this.maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));

        maintenance.setDescription(command.description());
        maintenance.setPrice(command.price());

        return this.maintenanceRepository.save(maintenance);
    }
}
