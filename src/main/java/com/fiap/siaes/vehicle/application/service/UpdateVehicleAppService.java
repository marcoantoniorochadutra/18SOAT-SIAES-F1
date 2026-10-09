package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVehicleAppService implements UpdateVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public Vehicle execute(VehicleId id, UpdateVehicleCommand command) {
        Vehicle vehicle = this.vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        vehicle.setBrand(command.brand());
        vehicle.setModel(command.model());
        vehicle.setYear(command.year());

        return this.vehicleRepository.save(vehicle);
    }
}
