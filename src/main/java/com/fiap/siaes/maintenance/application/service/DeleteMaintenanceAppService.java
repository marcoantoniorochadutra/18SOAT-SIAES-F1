package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.DeleteMaintenanceUseCase;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteMaintenanceAppService implements DeleteMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    @Transactional
    public void execute(MaintenanceId id) {
        this.maintenanceRepository.deleteById(id);
    }
}
