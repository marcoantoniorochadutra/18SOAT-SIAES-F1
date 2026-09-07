package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.ListMaintenancesUseCase;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListMaintenancesAppService implements ListMaintenancesUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    public List<Maintenance> execute() {
        return this.maintenanceRepository.findAll();
    }
}
