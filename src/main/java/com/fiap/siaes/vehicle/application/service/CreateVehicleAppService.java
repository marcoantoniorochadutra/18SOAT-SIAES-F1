package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateVehicleAppService implements CreateVehicleUseCase {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public VehicleId execute(CreateVehicleCommand command) {
        CustomerId ownerId = new CustomerId(command.ownerId());
        this.customerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + ownerId));

        if (this.vehicleRepository.existsByPlate(command.plate())) {
            throw new IllegalArgumentException("Já existe um veículo cadastrado com a placa: " + command.plate());
        }

        Vehicle vehicle = Vehicle.register(ownerId, command.plate(), command.brand(), command.model(), command.year());
        return this.vehicleRepository.save(vehicle).getId();
    }
}
