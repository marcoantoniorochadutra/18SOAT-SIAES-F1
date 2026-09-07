package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.GetMaintenanceUseCase;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMaintenanceAppService implements GetMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    public Maintenance execute(MaintenanceId id) {
        return this.maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));
    }
}
