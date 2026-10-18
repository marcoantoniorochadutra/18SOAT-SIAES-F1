package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.application.port.MaintenanceQueryPort;
import com.fiap.siaes.maintenance.application.usecase.ListMaintenancesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListMaintenancesAppService implements ListMaintenancesUseCase {

    private final MaintenanceQueryPort maintenanceQueryPort;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<MaintenanceResponse> execute(ListMaintenancesCommand command) {
        return this.maintenanceQueryPort.findAllByFilter(command.filter(), command.pageQuery());
    }
}
