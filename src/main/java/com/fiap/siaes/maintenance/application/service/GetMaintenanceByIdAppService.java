package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.application.usecase.GetMaintenanceByIdUseCase;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMaintenanceByIdAppService implements GetMaintenanceByIdUseCase {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public MaintenanceResponse execute(MaintenanceId id) {
        Maintenance maintenance = this.maintenanceRepository.findByIdOrThrowNotFound(id);

        return MaintenanceResponse.from(maintenance);
    }
}
