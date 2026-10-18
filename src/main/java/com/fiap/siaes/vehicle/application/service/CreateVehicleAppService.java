package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase;
import com.fiap.siaes.vehicle.domain.exception.VehiclePlateAlreadyExistsException;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateVehicleAppService implements CreateVehicleUseCase {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public VehicleId execute(CreateVehicleCommand command) {
        CustomerId customerId = this.resolveCustomerId(command);

        this.customerRepository.existsByIdOrThrowNotFound(customerId);

        Vehicle vehicle = this.createVehicle(command, customerId);

        this.validateVehicle(vehicle);

        this.vehicleRepository.save(vehicle);

        return vehicle.getId();
    }

    private CustomerId resolveCustomerId(CreateVehicleCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();

        if (authenticatedUser.isCustomer()) {
            return authenticatedUser.customerId();
        }

        return command.customerId();
    }

    private void validateVehicle(Vehicle vehicle) {
        if (this.vehicleRepository.existsByPlate(vehicle.getPlate())) {
            throw new VehiclePlateAlreadyExistsException(vehicle.getPlate().getValue());
        }
    }

    private Vehicle createVehicle(CreateVehicleCommand command, CustomerId customerId) {
        return Vehicle.builder()
                .customerId(customerId)
                .plate(command.plate())
                .brand(command.brand())
                .model(command.model())
                .year(command.year())
                .build();
    }
}
