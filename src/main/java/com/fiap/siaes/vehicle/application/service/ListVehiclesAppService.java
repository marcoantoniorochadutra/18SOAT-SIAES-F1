package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListVehiclesAppService implements ListVehiclesUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<VehicleResponse> execute(ListVehiclesCommand command) {
        return this.vehicleRepository.findAllByFilter(command.filter(), command.pageQuery());
    }
}
