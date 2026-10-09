package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.vehicle.application.usecase.GetVehicleUseCase;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetVehicleAppService implements GetVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    public Vehicle execute(VehicleId id) {
        return this.vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
    }
}
