package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListVehiclesAppService implements ListVehiclesUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> execute() {
        return this.vehicleRepository.findAll();
    }
}
