package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.port.VehicleQueryPort;
import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListVehiclesAppService implements ListVehiclesUseCase {

    private final VehicleQueryPort vehicleQueryPort;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<VehicleResponse> execute(ListVehiclesCommand command) {
        return this.vehicleQueryPort.findAllByFilter(command.filter(), command.pageQuery());
    }
}
