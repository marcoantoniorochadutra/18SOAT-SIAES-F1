package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase;
import com.fiap.siaes.vehicle.domain.model.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteVehicleAppService implements DeleteVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public void execute(VehicleId id) {
        this.vehicleRepository.deleteById(id);
    }
}
