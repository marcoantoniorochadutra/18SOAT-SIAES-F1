package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
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
public class UpdateVehicleAppService implements UpdateVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public VehicleId execute(UpdateVehicleCommand command) {
        Vehicle vehicle = this.findVehicleToUpdate(command);

        vehicle.update(command.plate(), command.brand(), command.model(), command.year());

        this.validateVehicle(vehicle);

        this.vehicleRepository.save(vehicle);

        return vehicle.getId();
    }

    private Vehicle findVehicleToUpdate(UpdateVehicleCommand command) {
        Vehicle vehicle = this.vehicleRepository.findByIdOrThrowNotFound(command.id());
        AuthenticatedUser authenticatedUser = command.authenticatedUser();

        if (authenticatedUser.isCustomer() && !vehicle.isOwnedBy(authenticatedUser.customerId())) {
            throw new VehicleNotFoundException(command.id().toString());
        }

        return vehicle;
    }

    private void validateVehicle(Vehicle vehicle) {
        if (this.vehicleRepository.existsByPlateAndIdNot(vehicle.getPlate(), vehicle.getId())) {
            throw new VehiclePlateAlreadyExistsException(vehicle.getPlate().getValue());
        }
    }
}
